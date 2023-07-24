package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class PickupObjectEvent extends MessageHandler {
    @Override
    public void handle() {
        int category = this.packet.readInt(); //10 = floorItem and 20 = wallItem
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        RoomItem item = room.getRoomItemManager().getRoomItemById(itemId);

        if (item == null || item instanceof InteractionPostIt) {
            return;
        }

        if (item.getOwnerId() == this.client.getHabbo().getHabboInfo().getId()) {
            room.getRoomItemManager().pickUpItem(item, this.client.getHabbo());
        } else {
            if (room.getRoomRightsManager().hasRights(this.client.getHabbo())) {
                if (this.client.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER)) {
                    item.setOwnerId(this.client.getHabbo().getHabboInfo().getId());
                } else {
                    if (!room.getRoomInfo().isRoomOwner(this.client.getHabbo())) {
                        if (item.getOwnerId() == room.getRoomInfo().getOwnerInfo().getId()) {
                            return;
                        }
                    }
                }

                room.getRoomItemManager().ejectUserItem(item);
            }
        }
    }
}
