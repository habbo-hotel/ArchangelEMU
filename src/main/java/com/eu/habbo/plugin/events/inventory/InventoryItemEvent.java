package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import lombok.Getter;

@Getter
public class InventoryItemEvent extends InventoryEvent {
    private final RoomItem item;

    public InventoryItemEvent(HabboInventory inventory, RoomItem item) {
        super(inventory);

        this.item = item;
    }
}
