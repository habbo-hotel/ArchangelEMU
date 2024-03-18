package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExtendedProfileChangedMessageComposer extends MessageComposer {
    private final int userId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.extendedProfileChangedMessageComposer);
        this.response.appendInt(this.userId);
        return this.response;
    }
}