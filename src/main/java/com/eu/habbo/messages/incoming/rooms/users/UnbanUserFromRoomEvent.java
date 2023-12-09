package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class UnbanUserFromRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(roomId);

        if (room != null) {
            if (room.getRoomInfo().isRoomOwner(this.client.getHabbo())) {
                room.getRoomInfractionManager().unbanHabbo(userId);
            }
        }

    }
}
