package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RoomWordFilterManager {
    private final Room room;

    private final List<String> filteredWords;

    public RoomWordFilterManager(Room room) {
        this.room = room;
        this.filteredWords = new ArrayList<>();
    }

    public synchronized void load(Connection connection) {
        this.filteredWords.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_wordfilter WHERE room_id = ?")) {
            statement.setInt(1, this.room.getRoomInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.filteredWords.add(set.getString("word"));
                }
            }
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Caught Exception", e);
        }
    }

    public void addWord(String word) {
        synchronized (this.filteredWords) {
            if (this.filteredWords.contains(word))
                return;


            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO room_wordfilter VALUES (?, ?)")) {
                statement.setInt(1, this.room.getRoomInfo().getId());
                statement.setString(2, word);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
                return;
            }

            this.filteredWords.add(word);
        }
    }

    public void removeWord(String word) {
        synchronized (this.filteredWords) {
            this.filteredWords.remove(word);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_wordfilter WHERE room_id = ? AND word = ?")) {
                statement.setInt(1, this.room.getRoomInfo().getId());
                statement.setString(2, word);
                statement.execute();
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }
}
