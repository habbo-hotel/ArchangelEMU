package com.eu.habbo.roleplay.messages.incoming.device;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class InfoScreenSaveEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemID = this.packet.readInt();
        RoomItem item = this.client.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemID);
        if (item == null) {
            this.client.getHabbo().shout("invalid item");
            return;
        }

        String content = this.packet.readString();

        if (content == null) {
            this.client.getHabbo().shout("missing content");
            return;
        }

        item.setExtraData(content);
        this.client.getHabbo().getRoomUnit().getRoom().updateItemState(item);
        this.client.getHabbo().shout("I SAVED STUFF");
    }
}