package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HotelViewCustomTimerComposer extends MessageComposer {
    private final String name;
    private final int seconds;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.hotelViewCustomTimerComposer);
        this.response.appendString(this.name); //Send by the client.
        this.response.appendInt(this.seconds);
        return this.response;
    }
}
