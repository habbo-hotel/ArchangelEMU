package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CallForHelpReplyMessageComposer extends MessageComposer {
    private final String message;

    public CallForHelpReplyMessageComposer(String message) {
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.callForHelpReplyMessageComposer);
        this.response.appendString(this.message);
        return this.response;
    }
}
