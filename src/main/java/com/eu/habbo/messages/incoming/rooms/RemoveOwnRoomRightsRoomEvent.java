package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RemoveOwnRoomRightsRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(roomId);
        room.getRoomRightsManager().removeRights(this.client.getHabbo().getHabboInfo().getId());
    }
}