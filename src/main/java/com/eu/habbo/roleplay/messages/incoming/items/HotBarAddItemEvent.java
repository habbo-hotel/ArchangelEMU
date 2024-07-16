package com.eu.habbo.roleplay.messages.incoming.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.FurniListRemoveComposer;
import com.eu.habbo.roleplay.messages.outgoing.items.HotBarListItemsComposer;

public class HotBarAddItemEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemID = this.packet.readInt();

        RoomItem itemFromInventory = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemID);

        if (itemFromInventory == null) {
            this.client.getHabbo().whisper("item not found in inventory.");
            return;
        }

        this.client.getHabbo().getInventory().getHotBarComponent().addItem(itemFromInventory);
        this.client.getHabbo().getInventory().getItemsComponent().removeHabboItem(itemID);

        itemFromInventory.setRoomId(-1);
        itemFromInventory.setSqlUpdateNeeded(true);
        Emulator.getThreading().run(itemFromInventory);

        this.client.sendResponse(new FurniListRemoveComposer(itemFromInventory.getId()));

        this.client.sendResponse(new HotBarListItemsComposer(this.client.getHabbo()));
    }
}