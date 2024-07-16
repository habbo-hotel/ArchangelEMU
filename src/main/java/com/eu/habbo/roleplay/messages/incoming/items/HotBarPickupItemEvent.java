package com.eu.habbo.roleplay.messages.incoming.items;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.FurniListAddOrUpdateComposer;
import com.eu.habbo.messages.outgoing.inventory.FurniListRemoveComposer;
import com.eu.habbo.roleplay.messages.outgoing.items.HotBarListItemsComposer;

public class HotBarPickupItemEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemID = this.packet.readInt();

        RoomItem itemFromHotBar = this.client.getHabbo().getInventory().getHotBarComponent().getItems().get(itemID);

        if (itemFromHotBar == null) {
            this.client.getHabbo().whisper("item not found in hot bar.");
            return;
        }

        this.client.getHabbo().getInventory().getItemsComponent().addItem(itemFromHotBar);
        this.client.getHabbo().getInventory().getHotBarComponent().removeItem(itemID);

        this.client.sendResponse(new FurniListAddOrUpdateComposer(itemFromHotBar));

        this.client.sendResponse(new HotBarListItemsComposer(this.client.getHabbo()));
    }
}