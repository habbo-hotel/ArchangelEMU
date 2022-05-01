package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomEntryInfoMessageComposer extends MessageComposer {
    private final Room room;
    private final boolean roomOwner;

    public RoomEntryInfoMessageComposer(Room room, boolean roomOwner) {
        this.room = room;
        this.roomOwner = roomOwner;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomEntryInfoMessageComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendBoolean(this.roomOwner);
        return this.response;
    }
}
