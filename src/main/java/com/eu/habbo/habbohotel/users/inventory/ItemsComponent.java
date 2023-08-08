package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.plugin.events.inventory.InventoryItemAddedEvent;
import com.eu.habbo.plugin.events.inventory.InventoryItemRemovedEvent;
import com.eu.habbo.plugin.events.inventory.InventoryItemsAddedEvent;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.NoSuchElementException;

@Slf4j
public class ItemsComponent {

    @Getter
    private final TIntObjectMap<RoomItem> items = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    private final HabboInventory inventory;

    public ItemsComponent(HabboInventory inventory, Habbo habbo) {
        this.inventory = inventory;
        this.items.putAll(loadItems(habbo));
    }

    public static THashMap<Integer, RoomItem> loadItems(Habbo habbo) {
        THashMap<Integer, RoomItem> itemsList = new THashMap<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE room_id = ? AND user_id = ?")) {
            statement.setInt(1, 0);
            statement.setInt(2, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    try {
                        RoomItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);

                        if (item != null) {
                            itemsList.put(set.getInt("id"), item);
                        } else {
                            log.error("Failed to load HabboItem: " + set.getInt("id"));
                        }
                    } catch (SQLException e) {
                        log.error("Caught SQL exception", e);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        return itemsList;
    }

    public void addItem(RoomItem item) {
        if (item == null) {
            return;
        }

        InventoryItemAddedEvent event = new InventoryItemAddedEvent(this.inventory, item);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled()) {
            return;
        }

        synchronized (this.items) {
            this.items.put(event.getItem().getId(), event.getItem());
        }
    }

    public void addItems(HashSet<RoomItem> items) {
        InventoryItemsAddedEvent event = new InventoryItemsAddedEvent(this.inventory, items);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled()) {
            return;
        }

        synchronized (this.items) {
            for (RoomItem item : event.items) {
                if (item == null) {
                    continue;
                }

                this.items.put(item.getId(), item);
            }
        }
    }

    public RoomItem getHabboItem(int itemId) {
        return this.items.get(Math.abs(itemId));
    }

    public RoomItem getAndRemoveHabboItem(final Item item) {
        final RoomItem[] roomItem = {null};
        synchronized (this.items) {
            this.items.forEachValue(object -> {
                if (object.getBaseItem() == item) {
                    roomItem[0] = object;
                    return false;
                }

                return true;
            });
        }
        this.removeHabboItem(roomItem[0]);
        return roomItem[0];
    }

    public void removeHabboItem(int itemId) {
        this.items.remove(itemId);
    }

    public void removeHabboItem(RoomItem item) {
        InventoryItemRemovedEvent event = new InventoryItemRemovedEvent(this.inventory, item);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled()) {
            return;
        }

        synchronized (this.items) {
            this.items.remove(event.getItem().getId());
        }
    }

    public THashSet<RoomItem> getItemsAsValueCollection() {
        THashSet<RoomItem> items = new THashSet<>();
        items.addAll(this.items.valueCollection());

        return items;
    }

    public int itemCount() {
        return this.items.size();
    }

    public void dispose() {
        synchronized (this.items) {
            TIntObjectIterator<RoomItem> items = this.items.iterator();

            if (items == null) {
                log.error("Items is NULL!");
                return;
            }

            if (!this.items.isEmpty()) {
                for (int i = this.items.size(); i-- > 0; ) {
                    try {
                        items.advance();
                    } catch (NoSuchElementException e) {
                        break;
                    }
                    RoomItem roomItem = items.value();
                    if (roomItem.isSqlUpdateNeeded())
                        Emulator.getThreading().run(items.value());
                }
            }

            this.items.clear();
        }
    }
}
