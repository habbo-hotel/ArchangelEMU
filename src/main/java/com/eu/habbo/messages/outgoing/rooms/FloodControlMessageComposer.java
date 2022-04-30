package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FloodControlMessageComposer extends MessageComposer {
    private final int time;

    public FloodControlMessageComposer(int time) {
        this.time = time;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.floodControlMessageComposer);
        this.response.appendInt(this.time);
        return this.response;
    }
}
