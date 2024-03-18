package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ChatReviewSessionOfferedToGuideMessageComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.chatReviewSessionOfferedToGuideMessageComposer);
        this.response.appendInt(Emulator.getConfig().getInt("guardians.accept.timer"));
        return this.response;
    }
}
