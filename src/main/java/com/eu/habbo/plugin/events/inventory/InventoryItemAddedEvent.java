package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class InventoryItemAddedEvent extends InventoryItemEvent {
    public InventoryItemAddedEvent(RoomItem item) {
        super(item);
    }
}
