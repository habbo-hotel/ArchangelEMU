package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuideTicketResolutionMessageComposer extends MessageComposer {
    public final static int CLOSED = 1;
    public final static int MISUSE = 2;

    public final int code;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideTicketResolutionMessageComposer);
        this.response.appendInt(this.code);
        return this.response;
    }
}
