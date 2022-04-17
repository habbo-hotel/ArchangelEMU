package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class WiredValidationErrorComposer extends MessageComposer {
    private final String message;

    public WiredValidationErrorComposer(String message) {
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.WiredValidationErrorComposer);
        this.response.appendString(this.message);
        return this.response;
    }
}
