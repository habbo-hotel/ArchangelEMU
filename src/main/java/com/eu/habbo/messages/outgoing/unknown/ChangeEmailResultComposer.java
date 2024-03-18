package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangeEmailResultComposer extends MessageComposer {
    private final int result;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.changeEmailResultComposer);
        this.response.appendInt(this.result);
        return this.response;
    }
}