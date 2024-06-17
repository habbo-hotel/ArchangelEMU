package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

import java.util.HashSet;

public class InventoryItemsAddedEvent extends InventoryEvent {
    public final HashSet<RoomItem> items;

    public InventoryItemsAddedEvent(HashSet<RoomItem> items) {
        this.items = items;
    }
}
