package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.messages.incoming.MessageHandler;

public class BanUserWithDurationEvent extends MessageHandler {
    @Override
    public void handle() {
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();
        String banName = this.packet.readString();

        Emulator.getGameEnvironment().getRoomManager().banUserFromRoom(this.client.getHabbo(), userId, roomId, RoomManager.RoomBanTypes.valueOf(banName));
    }
}