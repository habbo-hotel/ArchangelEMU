package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HotelWillCloseInMinutesComposer extends MessageComposer {
    private final int minutes;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.hotelWillCloseInMinutesComposer);
        this.response.appendInt(this.minutes);
        return this.response;
    }
}
