package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.FloorHeightMapComposer;
import com.eu.habbo.messages.outgoing.rooms.HeightMapComposer;

public class GetRoomEntryDataEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getRoomUnit().isLoadingRoom()) {
            Room room = this.client.getHabbo().getRoomUnit().getLoadingRoom();

            if (room != null && room.getLayout() != null) {
                this.client.sendResponse(new HeightMapComposer(room));

                this.client.sendResponse(new FloorHeightMapComposer(room));

                Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), room);
            }
        }
    }
}
