package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.YouAreControllerMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.NoSuchFlatComposer;

public class RemoveAllRightsEvent extends MessageHandler {
    @Override
    public void handle() {
        final Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null || room.getRoomInfo().getId() != this.packet.readInt())
            return;

        if (room.getRoomInfo().getOwnerInfo().getId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER)) {
            room.getRoomRightsManager().getRights().forEach(value -> {
                Habbo habbo = room.getRoomUnitManager().getRoomHabboById(value);

                    if (habbo != null) {
                        room.sendComposer(new NoSuchFlatComposer(room, value).compose());
                        habbo.getRoomUnit().removeStatus(RoomUnitStatus.FLAT_CONTROL);
                        habbo.getClient().sendResponse(new YouAreControllerMessageComposer(RoomRightLevels.NONE));
                    }
            });

            room.getRoomRightsManager().removeAllRights();
        }
    }
}
