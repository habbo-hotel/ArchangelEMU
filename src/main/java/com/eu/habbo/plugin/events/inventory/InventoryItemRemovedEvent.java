package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;

public class InventoryItemRemovedEvent extends InventoryItemEvent {
    public InventoryItemRemovedEvent(HabboInventory inventory, RoomItem item) {
        super(inventory, item);
    }
}
