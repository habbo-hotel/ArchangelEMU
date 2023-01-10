package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomSettingsDataComposer;

public class GetRoomSettingsEvent extends MessageHandler {
    @Override
    public void handle() {
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if (room != null)
            this.client.sendResponse(new RoomSettingsDataComposer(room));
    }
}
