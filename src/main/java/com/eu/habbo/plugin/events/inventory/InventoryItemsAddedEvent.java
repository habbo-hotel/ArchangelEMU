package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import gnu.trove.set.hash.THashSet;

public class InventoryItemsAddedEvent extends InventoryEvent {
    public final THashSet<RoomItem> items;

    public InventoryItemsAddedEvent(HabboInventory inventory, THashSet<RoomItem> items) {
        super(inventory);
        this.items = items;
    }
}
