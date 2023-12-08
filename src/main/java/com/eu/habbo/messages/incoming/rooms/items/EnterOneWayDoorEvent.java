package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class EnterOneWayDoorEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        int itemId = this.packet.readInt();
        RoomItem item = this.client.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemId);

        if (item == null)
            return;

        if (item instanceof InteractionOneWayGate) {
            if (!item.getExtraData().equals("0") || this.client.getHabbo().getRoomUnit().isTeleporting())
                return;

            item.onClick(this.client, this.client.getHabbo().getRoomUnit().getRoom(), null);
        }

    }
}
