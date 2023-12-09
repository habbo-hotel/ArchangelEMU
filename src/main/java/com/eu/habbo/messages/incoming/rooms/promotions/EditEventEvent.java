package com.eu.habbo.messages.incoming.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.promotions.RoomPromotion;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.promotions.RoomEventComposer;


public class EditEventEvent extends MessageHandler {

    @Override
    public void handle() {

        int id = this.packet.readInt();
        String promotionName = this.packet.readString();
        String promotionDescription = this.packet.readString();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(id);

        if (room == null || room.getRoomInfo().getOwnerInfo().getId() != this.client.getHabbo().getHabboInfo().getId() || !this.client.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER)) {
            return;
        }

        RoomPromotion roomPromotion = room.getRoomPromotionManager().getPromotion();

        if (roomPromotion != null) {

            roomPromotion.setTitle(promotionName);
            roomPromotion.setDescription(promotionDescription);

            roomPromotion.needsUpdate = true;
            roomPromotion.save();

            room.sendComposer(new RoomEventComposer(room, roomPromotion).compose());
        }
    }
}
