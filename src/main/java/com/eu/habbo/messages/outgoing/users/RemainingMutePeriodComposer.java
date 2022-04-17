package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RemainingMutePeriodComposer extends MessageComposer {
    private final int seconds;

    public RemainingMutePeriodComposer(int seconds) {
        this.seconds = seconds;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.RemainingMutePeriodComposer);
        this.response.appendInt(this.seconds);
        return this.response;
    }
}
