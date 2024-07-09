package com.eu.habbo.roleplay.messages.incoming.device;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.interactions.InteractionPhone;
import com.eu.habbo.roleplay.interactions.InteractionUsable;

public class DeviceOpenEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemID = this.packet.readInt();
        RoomItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemID);

        if (item == null) {
            this.client.getHabbo().shout("invalid item");
            return;
        }

        if (!InteractionUsable.class.isAssignableFrom(item.getBaseItem().getInteractionType().getType())) {
            this.client.getHabbo().shout("device is not usable");
            return;
        }

        if (InteractionPhone.class.isAssignableFrom(item.getBaseItem().getInteractionType().getType())) {
            this.client.getHabbo().shout("I USED A PHONE");
            return;
        }

        throw new RuntimeException("unsupported item");
    }
}