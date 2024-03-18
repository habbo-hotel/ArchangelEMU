package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CanCreateRoomComposer extends MessageComposer {
    private final int count;
    private final int max;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.canCreateRoomComposer);

        this.response.appendInt(this.count >= this.max ? 1 : 0);
        this.response.appendInt(this.max);

        return this.response;
    }
}