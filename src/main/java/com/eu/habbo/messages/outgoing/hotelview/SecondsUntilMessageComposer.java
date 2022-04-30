package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class SecondsUntilMessageComposer extends MessageComposer {
    private final String dateString;
    private final int seconds;

    public SecondsUntilMessageComposer(String dateString, int seconds) {
        this.dateString = dateString;
        this.seconds = seconds;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.secondsUntilMessageComposer);
        this.response.appendString(this.dateString);
        this.response.appendInt(this.seconds);

        return this.response;
    }
}
