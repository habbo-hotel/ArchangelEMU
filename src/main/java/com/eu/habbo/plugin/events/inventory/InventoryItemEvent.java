package com.eu.habbo.plugin.events.inventory;

import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.Getter;

@Getter
public class InventoryItemEvent extends InventoryEvent {
    private final HabboItem item;

    public InventoryItemEvent(HabboInventory inventory, HabboItem item) {
        super(inventory);

        this.item = item;
    }
}
