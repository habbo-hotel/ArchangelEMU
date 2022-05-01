package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GroupDetailsChangedMessageComposer extends MessageComposer {
    private final int guildId;

    public GroupDetailsChangedMessageComposer(int guildId) {
        this.guildId = guildId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.groupDetailsChangedMessageComposer);
        this.response.appendInt(this.guildId);
        return this.response;
    }
}