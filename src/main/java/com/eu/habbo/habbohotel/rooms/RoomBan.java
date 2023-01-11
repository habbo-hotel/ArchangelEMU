package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.database.DatabaseConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Getter
@AllArgsConstructor
public class RoomBan {

    private  final int roomId;
    private  final int userId;
    private  final String username;
    private  final int endTimestamp;

    public RoomBan(ResultSet set) throws SQLException {
        this.roomId = set.getInt("room_id");
        this.userId = set.getInt(DatabaseConstants.USER_ID);
        this.username = set.getString("username");
        this.endTimestamp = set.getInt("ends");
    }


    public void insert() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_bans (room_id, user_id, ends) VALUES (?, ?, ?)")) {
            statement.setInt(1, this.roomId);
            statement.setInt(2, this.userId);
            statement.setInt(3, this.endTimestamp);
            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }


    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_bans WHERE room_id = ? AND user_id = ?")) {
            statement.setInt(1, this.roomId);
            statement.setInt(2, this.userId);
            statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }
}
