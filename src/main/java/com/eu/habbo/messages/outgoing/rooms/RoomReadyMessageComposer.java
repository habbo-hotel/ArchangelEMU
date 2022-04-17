package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomReadyMessageComposer extends MessageComposer {
    private final Room room;

    public RoomReadyMessageComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.RoomReadyMessageComposer);
        this.response.appendString(this.room.getLayout().getName());
        this.response.appendInt(this.room.getId());
        return this.response;
    }
}
