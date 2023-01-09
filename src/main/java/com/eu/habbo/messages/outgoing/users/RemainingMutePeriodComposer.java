package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RemainingMutePeriodComposer extends MessageComposer {
    private final int seconds;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.remainingMutePeriodComposer);
        this.response.appendInt(this.seconds);
        return this.response;
    }
}
