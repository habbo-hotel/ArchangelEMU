package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ObjectUpdateMessageComposer;

public class MoveObjectEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        int furniId = this.packet.readInt();
        HabboItem item = room.getHabboItem(furniId);
        if (item == null) return;

        int x = this.packet.readInt();
        int y = this.packet.readInt();
        int rotation = this.packet.readInt();
        RoomTile tile = room.getLayout().getTile((short) x, (short) y);

        FurnitureMovementError error = room.canPlaceFurnitureAt(item, this.client.getHabbo(), tile, rotation);
        if (!error.equals(FurnitureMovementError.NONE)) {
            this.client.sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.getKey(), error.getErrorCode()));
            this.client.sendResponse(new ObjectUpdateMessageComposer(item));
            return;
        }

        error = room.moveFurniTo(item, tile, rotation, this.client.getHabbo());
        if (!error.equals(FurnitureMovementError.NONE)) {
            this.client.sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.getKey(), error.getErrorCode()));
            this.client.sendResponse(new ObjectUpdateMessageComposer(item));
        }
    }
}