package com.eu.habbo.habbohotel.wired.highscores;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class WiredHighscoreManager {
    private final HashMap<Integer, List<WiredHighscoreDataEntry>> data = new HashMap<>();
    
    private final static String locale = (System.getProperty("user.language") != null ? System.getProperty("user.language") : "en");
    private final static String country = (System.getProperty("user.country") != null ? System.getProperty("user.country") : "US");

    private final static DayOfWeek firstDayOfWeek = WeekFields.of(new Locale(locale, country)).getFirstDayOfWeek();
    private final static DayOfWeek lastDayOfWeek = DayOfWeek.of(((firstDayOfWeek.getValue() + 5) % DayOfWeek.values().length) + 1);
    private final static ZoneId zoneId = ZoneId.systemDefault();

    public static ScheduledFuture midnightUpdater = null;

    public void load() {
        long millis = System.currentTimeMillis();

        this.data.clear();
        this.loadHighscoreData();

        log.info("Highscore Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS, " + this.data.size() + " items)");
    }

    @EventHandler
    public static void onEmulatorLoaded(EmulatorLoadedEvent event) {
        if (midnightUpdater != null) {
            midnightUpdater.cancel(true);
        }
        
        midnightUpdater = Emulator.getThreading().run(new WiredHighscoreMidnightUpdater(), WiredHighscoreMidnightUpdater.getNextUpdaterRun());
    }

    public void dispose() {
        if (midnightUpdater != null) {
            midnightUpdater.cancel(true);
        }

        this.data.clear();
    }

    private void loadHighscoreData() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM items_highscore_data")) {
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    WiredHighscoreDataEntry entry = new WiredHighscoreDataEntry(set);

                    if (!this.data.containsKey(entry.getItemId())) {
                        this.data.put(entry.getItemId(), new ArrayList<>());
                    }

                    this.data.get(entry.getItemId()).add(entry);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public void addHighscoreData(WiredHighscoreDataEntry entry) {
        if (!this.data.containsKey(entry.getItemId())) {
            this.data.put(entry.getItemId(), new ArrayList<>());
        }

        this.data.get(entry.getItemId()).add(entry);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO `items_highscore_data` (`item_id`, `user_ids`, `score`, `is_win`, `timestamp`) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, entry.getItemId());
            statement.setString(2, entry.getUserIds().stream().map(Object::toString).collect(Collectors.joining(",")));
            statement.setInt(3, entry.getScore());
            statement.setInt(4, entry.isWin() ? 1 : 0);
            statement.setInt(5, entry.getTimestamp());

            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public List<WiredHighscoreRow> getHighscoreRowsForItem(int itemId, WiredHighscoreClearType clearType, WiredHighscoreScoreType scoreType) {
        if (!this.data.containsKey(itemId)) return null;

        Stream<WiredHighscoreRow> highscores = new ArrayList<>(this.data.get(itemId)).stream()
                .filter(entry -> this.timeMatchesEntry(entry, clearType) && (scoreType != WiredHighscoreScoreType.MOSTWIN || entry.isWin()))
                .map(entry -> new WiredHighscoreRow(
                        entry.getUserIds().stream()
                                .map(id -> Emulator.getGameEnvironment().getHabboManager().getHabboInfo(id).getUsername())
                                .collect(Collectors.toList()),
                        entry.getScore()
                ));

        if (scoreType == WiredHighscoreScoreType.CLASSIC) {
            return highscores.sorted(WiredHighscoreRow::compareTo).collect(Collectors.toList());
        }

        if (scoreType == WiredHighscoreScoreType.PERTEAM) {
            return highscores
                    .collect(Collectors.groupingBy(h -> h.getUsers().hashCode()))
                    .values()
                    .stream()
                    .map(wiredHighscoreRows -> wiredHighscoreRows.stream()
                            .sorted(WiredHighscoreRow::compareTo).toList()
                            .get(0)
                    )
                    .sorted(WiredHighscoreRow::compareTo)
                    .collect(Collectors.toList());
        }

        if (scoreType == WiredHighscoreScoreType.MOSTWIN) {
            return highscores
                    .collect(Collectors.groupingBy(h -> h.getUsers().hashCode()))
                    .values()
                    .stream()
                    .map(wiredHighscoreRows -> new WiredHighscoreRow(wiredHighscoreRows.get(0).getUsers(), wiredHighscoreRows.size()))
                    .sorted(WiredHighscoreRow::compareTo)
                    .collect(Collectors.toList());
        }

        return null;
    }

    private boolean timeMatchesEntry(WiredHighscoreDataEntry entry, WiredHighscoreClearType timeType) {
        return switch (timeType) {
            case DAILY ->
                    entry.getTimestamp() > this.getTodayStartTimestamp() && entry.getTimestamp() < this.getTodayEndTimestamp();
            case WEEKLY ->
                    entry.getTimestamp() > this.getWeekStartTimestamp() && entry.getTimestamp() < this.getWeekEndTimestamp();
            case MONTHLY ->
                    entry.getTimestamp() > this.getMonthStartTimestamp() && entry.getTimestamp() < this.getMonthEndTimestamp();
            case ALLTIME -> true;
        };

    }

    private long getTodayStartTimestamp() {
        return LocalDateTime.now().with(LocalTime.MIDNIGHT).atZone(zoneId).toEpochSecond();
    }

    private long getTodayEndTimestamp() {
        return LocalDateTime.now().with(LocalTime.MIDNIGHT).plusDays(1).plusSeconds(-1).atZone(zoneId).toEpochSecond();
    }

    private long getWeekStartTimestamp() {
        return LocalDateTime.now().with(LocalTime.MIDNIGHT).with(TemporalAdjusters.previousOrSame(firstDayOfWeek)).atZone(zoneId).toEpochSecond();
    }

    private long getWeekEndTimestamp() {
        return LocalDateTime.now().with(LocalTime.MIDNIGHT).plusDays(1).plusSeconds(-1).with(TemporalAdjusters.nextOrSame(lastDayOfWeek)).atZone(zoneId).toEpochSecond();
    }

    private long getMonthStartTimestamp() {
        return LocalDateTime.now().with(LocalTime.MIDNIGHT).with(TemporalAdjusters.firstDayOfMonth()).atZone(zoneId).toEpochSecond();
    }

    private long getMonthEndTimestamp() {
        return LocalDateTime.now().with(LocalTime.MIDNIGHT).plusDays(1).plusSeconds(-1).with(TemporalAdjusters.lastDayOfMonth()).atZone(zoneId).toEpochSecond();
    }
}
