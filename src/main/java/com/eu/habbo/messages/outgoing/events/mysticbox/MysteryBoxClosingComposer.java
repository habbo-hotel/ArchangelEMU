package com.eu.habbo.messages.outgoing.events.mysticbox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MysteryBoxClosingComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.MysteryBoxClosingComposer);
        return this.response;
    }
}
