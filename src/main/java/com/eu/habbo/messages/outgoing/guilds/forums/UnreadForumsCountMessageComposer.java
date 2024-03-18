package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnreadForumsCountMessageComposer extends MessageComposer {
    public final int count;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.unreadForumsCountMessageComposer);
        this.response.appendInt(this.count);
        return this.response;
    }
}