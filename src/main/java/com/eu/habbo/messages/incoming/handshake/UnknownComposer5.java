package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownComposer5 extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.UnknownComposer5);
        this.response.appendString(""); //Box color
        this.response.appendString(""); //Key color
        return this.response;
    }
}