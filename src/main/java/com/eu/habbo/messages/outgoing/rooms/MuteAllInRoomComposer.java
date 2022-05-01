package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MuteAllInRoomComposer extends MessageComposer {
    private final Room room;

    public MuteAllInRoomComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.muteAllInRoomComposer);
        this.response.appendBoolean(this.room.isMuted());
        return this.response;
    }
}
