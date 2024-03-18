package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GroupDetailsChangedMessageComposer extends MessageComposer {
    private final int guildId;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.groupDetailsChangedMessageComposer);
        this.response.appendInt(this.guildId);
        return this.response;
    }
}