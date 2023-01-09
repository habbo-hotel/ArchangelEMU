package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboGroupDeactivatedMessageComposer extends MessageComposer {
    private final int guildId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboGroupDeactivatedMessageComposer);
        this.response.appendInt(this.guildId);
        return this.response;
    }
}
