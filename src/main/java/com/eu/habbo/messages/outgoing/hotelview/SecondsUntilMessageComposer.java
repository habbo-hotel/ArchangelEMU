package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SecondsUntilMessageComposer extends MessageComposer {
    private final String dateString;
    private final int seconds;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.secondsUntilMessageComposer);
        this.response.appendString(this.dateString);
        this.response.appendInt(this.seconds);

        return this.response;
    }
}
