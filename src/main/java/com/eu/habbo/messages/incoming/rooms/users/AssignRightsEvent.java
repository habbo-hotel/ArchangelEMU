package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.users.UserRightsGivenEvent;

public class AssignRightsEvent extends MessageHandler {
    @Override
    public void handle() {
        int targetId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        if (room.getRoomInfo().isRoomOwner(this.client.getHabbo())  || this.client.getHabbo().hasRight(Permission.ACC_ANYROOMOWNER)) {
            Habbo target = room.getRoomUnitManager().getRoomHabboById(targetId);

            if (target != null) {
                if (!Emulator.getPluginManager().fireEvent(new UserRightsGivenEvent(this.client.getHabbo(), target)).isCancelled()) {
                    room.getRoomRightsManager().giveRights(target);
                }
            } else {
                MessengerBuddy buddy = this.client.getHabbo().getMessenger().getFriend(targetId);

                if (buddy != null) {
                    room.getRoomRightsManager().giveRights(buddy);
                }
            }
        }
    }
}
