package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.rooms.constants.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ObjectUpdateMessageComposer;

public class MoveObjectEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        int itemId = this.packet.readInt();

        RoomItem item = room.getRoomItemManager().getRoomItemById(itemId);

        if (item == null) {
            return;
        }

        int x = this.packet.readInt();
        int y = this.packet.readInt();
        int rotation = this.packet.readInt();

        RoomTile tile = room.getLayout().getTile((short) x, (short) y);

        FurnitureMovementError error = room.getRoomItemManager().moveItemTo(item, tile, rotation, this.client.getHabbo());

        if (!error.equals(FurnitureMovementError.NONE)) {
            this.client.sendResponse(new NotificationDialogMessageComposer(BubbleAlertKeys.FURNITURE_PLACEMENT_ERROR.getKey(), error.getErrorCode()));
            this.client.sendResponse(new ObjectUpdateMessageComposer(item));
        }
    }
}