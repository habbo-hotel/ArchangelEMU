package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.constants.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.constants.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;

public class MoveWallItemEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null)
            return;

        if (!room.getRoomRightsManager().hasRights(this.client.getHabbo()) && !this.client.getHabbo().hasPermissionRight(Permission.ACC_PLACEFURNI)) {
            if (!(room.getRoomInfo().getGuild().getId() > 0 && room.getGuildRightLevel(this.client.getHabbo()).isEqualOrGreaterThan(RoomRightLevels.GUILD_RIGHTS))) {
                this.client.sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.getKey(), FurnitureMovementError.NO_RIGHTS.getErrorCode()));
                return;
            }
        }

        int itemId = this.packet.readInt();
        String wallPosition = this.packet.readString();

        if (itemId <= 0 || wallPosition.length() <= 13)
            return;

        RoomItem item = room.getRoomItemManager().getRoomItemById(itemId);

        if (item == null)
            return;

        item.setWallPosition(wallPosition);
        item.setSqlUpdateNeeded(true);
        room.updateItem(item);
    }
}
