package com.eu.habbo.habbohotel.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.types.IRoomManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Slf4j
@Getter
public class RoomPromotionManager extends IRoomManager {

    private RoomPromotion promotion;

    public RoomPromotionManager(Room room) {
        super(room);
    }

    public void loadPromotions(Connection connection) {
        if (room.getRoomInfo().isPromoted()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_promotions WHERE room_id = ? AND end_timestamp > ? LIMIT 1")) {
                statement.setInt(1, room.getRoomInfo().getId());
                statement.setInt(2, Emulator.getIntUnixTimestamp());

                try (ResultSet promotionSet = statement.executeQuery()) {
                    room.getRoomInfo().setPromoted(false);
                    if (promotionSet.next()) {
                        room.getRoomInfo().setPromoted(true);
                        this.promotion = new RoomPromotion(room, promotionSet);
                    }
                }
            } catch (SQLException e) {
                log.error(CAUGHT_SQL_EXCEPTION, e);
            }
        }
    }

    public boolean isPromoted() {
        room.getRoomInfo().setPromoted(this.promotion != null && this.promotion.getEndTimestamp() > Emulator.getIntUnixTimestamp());
        room.setNeedsUpdate(true);
        return room.getRoomInfo().isPromoted();
    }

    public void createPromotion(String title, String description, int category) {
        room.getRoomInfo().setPromoted(true);

        if (this.promotion == null) {
            this.promotion = new RoomPromotion(room, title, description, Emulator.getIntUnixTimestamp() + (120 * 60), Emulator.getIntUnixTimestamp(), category);
        } else {
            this.promotion.setTitle(title);
            this.promotion.setDescription(description);
            this.promotion.setEndTimestamp(Emulator.getIntUnixTimestamp() + (120 * 60));
            this.promotion.setCategory(category);
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_promotions (room_id, title, description, end_timestamp, start_timestamp, category) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE title = ?, description = ?, end_timestamp = ?, category = ?")) {
            statement.setInt(1, room.getRoomInfo().getId());
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setInt(4, this.promotion.getEndTimestamp());
            statement.setInt(5, this.promotion.getStartTimestamp());
            statement.setInt(6, category);
            statement.setString(7, this.promotion.getTitle());
            statement.setString(8, this.promotion.getDescription());
            statement.setInt(9, this.promotion.getEndTimestamp());
            statement.setInt(10, this.promotion.getCategory());
            statement.execute();
        } catch (SQLException e) {
            log.error(CAUGHT_SQL_EXCEPTION, e);
        }

        room.setNeedsUpdate(true);
    }
}
