package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.HabboInventory;

import java.util.HashSet;

public class InventoryItemsAddedEvent extends InventoryEvent {
    public final HashSet<RoomItem> items;

    public InventoryItemsAddedEvent(HabboInventory inventory, HashSet<RoomItem> items) {
        super(inventory);
        this.items = items;
    }
}
