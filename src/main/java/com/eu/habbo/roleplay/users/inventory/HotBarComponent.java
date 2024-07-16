package com.eu.habbo.roleplay.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInventory;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotBarComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponsComponent.class);

    @Getter
    private final THashMap<Integer, RoomItem> items = new THashMap<Integer, RoomItem>();

    public final Habbo habbo;

    public HotBarComponent(Habbo habbo) {
        this.habbo = habbo;
        this.items.putAll(loadItems());
    }

    public THashMap<Integer, RoomItem> loadItems() {
        THashMap<Integer, RoomItem> itemsList = new THashMap<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE room_id = ? AND user_id = ?")) {
            statement.setInt(1, -1);
            statement.setInt(2, this.habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    try {
                        RoomItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);

                        if (item != null) {
                            itemsList.put(set.getInt("id"), item);
                        } else {
                            HotBarComponent.LOGGER.error("Failed to load HabboItem: " + set.getInt("id"));
                        }
                    } catch (SQLException e) {
                        HotBarComponent.LOGGER.error("Caught SQL exception", e);
                    }
                }
            }
        } catch (SQLException e) {
            HotBarComponent.LOGGER.error("Caught SQL exception", e);
        }

        return itemsList;
    }

    public void addItem(RoomItem item) {
        this.items.put(item.getId(), item);
    }

    public void removeItem(int itemId) {
        this.items.remove(itemId);
    }

    public void dispose() {
        synchronized (this.items) {
            this.items.clear();
        }
    }

}