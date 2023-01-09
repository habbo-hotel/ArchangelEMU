package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DisconnectReasonComposer extends MessageComposer {
    private final int errorCode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.disconnectReasonComposer);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}