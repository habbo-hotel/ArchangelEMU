package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import lombok.Getter;

@Getter
public class InventoryItemEvent extends InventoryEvent {
    private final RoomItem item;

    public InventoryItemEvent(RoomItem item) {
        this.item = item;
    }
}
