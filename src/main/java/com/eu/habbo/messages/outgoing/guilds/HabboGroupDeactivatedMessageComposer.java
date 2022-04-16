package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HabboGroupDeactivatedMessageComposer extends MessageComposer {
    private int guildId;

    public HabboGroupDeactivatedMessageComposer(int guildId) {
        this.guildId = guildId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.HabboGroupDeactivatedMessageComposer);
        this.response.appendInt(this.guildId);
        return this.response;
    }
}
