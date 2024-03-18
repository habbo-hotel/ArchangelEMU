package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HotelClosesAndWillOpenAtComposer extends MessageComposer {
    private final int hour;
    private final int minute;
    private final boolean disconnected;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.hotelClosesAndWillOpenAtComposer);
        this.response.appendInt(this.hour);
        this.response.appendInt(this.minute);
        this.response.appendBoolean(this.disconnected);
        return this.response;
    }
}
