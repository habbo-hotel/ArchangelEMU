package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlatAccessibleMessageComposer extends MessageComposer {
    private final String username;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.flatAccessibleMessageComposer);
        this.response.appendString(this.username);
        return this.response;
    }
}