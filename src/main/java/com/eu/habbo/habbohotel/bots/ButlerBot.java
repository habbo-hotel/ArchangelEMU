package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.chat.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;
import com.eu.habbo.plugin.events.bots.BotServerItemEvent;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToRoomUnit;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class ButlerBot extends Bot {
    public static THashMap<THashSet<String>, Integer> serveItems = new THashMap<>();

    public ButlerBot(ResultSet set) throws SQLException {
        super(set);
    }

    public static void initialise() {
        if (serveItems == null)
            serveItems = new THashMap<>();

        serveItems.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM bot_serves")) {
            while (set.next()) {
                String[] keys = set.getString("keys").split(";");
                THashSet<String> ks = new THashSet<>();
                Collections.addAll(ks, keys);
                serveItems.put(ks, set.getInt("item"));
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public static void dispose() {
        serveItems.clear();
    }

    @Override
    public void onUserSay(final RoomChatMessage message) {
        if (this.roomUnit.hasStatus(RoomUnitStatus.MOVE) || this.roomUnit.getRoom() == null) {
            return;
        }

        double distanceBetweenBotAndHabbo = this.roomUnit.getCurrentPosition().distance(message.getHabbo().getRoomUnit().getCurrentPosition());

        if (distanceBetweenBotAndHabbo <= Emulator.getConfig().getInt("hotel.bot.butler.commanddistance")) {

            if (message.getUnfilteredMessage() != null) {
                for (Map.Entry<THashSet<String>, Integer> set : serveItems.entrySet()) {
                    for (String keyword : set.getKey()) {

                        // Check if the string contains a certain keyword using a regex.
                        // If keyword = tea, teapot wouldn't trigger it.
                        if (message.getUnfilteredMessage().toLowerCase().matches("\\b" + keyword + "\\b")) {

                            // Enable plugins to cancel this event
                            BotServerItemEvent serveEvent = new BotServerItemEvent(this, message.getHabbo(), set.getValue());
                            if (Emulator.getPluginManager().fireEvent(serveEvent).isCancelled()) {
                                return;
                            }

                            // Start give handitem process
                            if (this.roomUnit.isCanWalk()) {
                                final String key = keyword;
                                final Bot bot = this;

                                // Step 1: Look at Habbo
                                bot.getRoomUnit().lookAtPoint(serveEvent.getHabbo().getRoomUnit().getCurrentPosition());

                                // Step 2: Prepare tasks for when the Bot (carrying the handitem) reaches the Habbo
                                final List<Runnable> tasks = new ArrayList<>();
                                tasks.add(new RoomUnitGiveHanditem(serveEvent.getHabbo().getRoomUnit(), serveEvent.getHabbo().getRoomUnit().getRoom(), serveEvent.getItemId()));
                                tasks.add(new RoomUnitGiveHanditem(this.roomUnit, serveEvent.getHabbo().getRoomUnit().getRoom(), 0));

                                tasks.add(() -> {
                                    if (this.roomUnit.getRoom() != null) {
                                        String botMessage = Emulator.getTexts()
                                                .getValue("bots.butler.given")
                                                .replace("%key%", key)
                                                .replace("%username%", serveEvent.getHabbo().getHabboInfo().getUsername());

                                        if (!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.roomUnit, this.roomUnit.getRoom(), new Object[]{botMessage})) {
                                            bot.talk(botMessage);
                                        }
                                    }
                                });

                                List<Runnable> failedReached = new ArrayList<>();
                                failedReached.add(() -> {
                                    if (distanceBetweenBotAndHabbo <= Emulator.getConfig().getInt("hotel.bot.butler.servedistance", 8)) {
                                        for (Runnable task : tasks) {
                                            task.run();
                                        }
                                    }
                                });

                                // Give bot the handitem that it's going to give the Habbo
                                Emulator.getThreading().run(new RoomUnitGiveHanditem(this.roomUnit, serveEvent.getHabbo().getRoomUnit().getRoom(), serveEvent.getItemId()));

                                if (distanceBetweenBotAndHabbo > Emulator.getConfig().getInt("hotel.bot.butler.reachdistance", 3)) {
                                    Emulator.getThreading().run(new RoomUnitWalkToRoomUnit(this.roomUnit, serveEvent.getHabbo().getRoomUnit(), serveEvent.getHabbo().getRoomUnit().getRoom(), tasks, failedReached, Emulator.getConfig().getInt("hotel.bot.butler.reachdistance", 3)));
                                } else {
                                    Emulator.getThreading().run(failedReached.get(0), 1000);
                                }
                            } else {
                                if (this.roomUnit.getRoom() != null) {
                                    serveEvent.getHabbo().getRoomUnit().setHandItem(serveEvent.getItemId());
                                    this.roomUnit.getRoom().sendComposer(new CarryObjectMessageComposer(serveEvent.getHabbo().getRoomUnit()).compose());

                                    String msg = Emulator.getTexts().getValue("bots.butler.given").replace("%key%", keyword).replace("%username%", serveEvent.getHabbo().getHabboInfo().getUsername());
                                    if (!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, this.roomUnit, this.roomUnit.getRoom(), new Object[]{msg})) {
                                        this.talk(msg);
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
