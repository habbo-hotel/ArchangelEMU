package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuideSessionPartnerIsTypingComposer extends MessageComposer {
    private final boolean typing;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideSessionPartnerIsTypingComposer);
        this.response.appendBoolean(this.typing);
        return this.response;
    }
}
