package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomForwardMessageComposer extends MessageComposer {
    private final int roomId;

    public RoomForwardMessageComposer(int roomId) {
        this.roomId = roomId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomForwardMessageComposer);
        this.response.appendInt(this.roomId);
        return this.response;
    }
}
