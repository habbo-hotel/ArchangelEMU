package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.CloseConnectionMessageComposer;

public class QuitEvent extends MessageHandler {
    @Override
    public void handle() {
        RoomHabbo roomHabbo = this.client.getHabbo().getRoomUnit();

        roomHabbo.setLoadingRoom(null);

        if (roomHabbo.getRoom() != null) {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(this.client.getHabbo(), roomHabbo.getRoom());
        }

        if (this.client.getHabbo().getHabboInfo().getRoomQueueId() != 0) {
            Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(this.client.getHabbo().getHabboInfo().getRoomQueueId());

            if (room != null) {
                room.removeFromQueue(this.client.getHabbo());
            } else {
                this.client.getHabbo().getHabboInfo().setRoomQueueId(0);
            }
            this.client.sendResponse(new CloseConnectionMessageComposer());
        }

        roomHabbo.clear();
        roomHabbo.setInRoom(false);
    }
}
