package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.MuteAllInRoomComposer;

public class MuteAllInRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null) {
            if (room.isOwner(this.client.getHabbo())) {
                room.setMuted(!room.isMuted());
                this.client.sendResponse(new MuteAllInRoomComposer(room));
            }
        }
    }
}
