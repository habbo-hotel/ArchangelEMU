package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CallForHelpReplyMessageComposer extends MessageComposer {
    private final String message;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.callForHelpReplyMessageComposer);
        this.response.appendString(this.message);
        return this.response;
    }
}
