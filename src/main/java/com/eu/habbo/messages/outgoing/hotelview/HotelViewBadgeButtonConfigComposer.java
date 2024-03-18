package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HotelViewBadgeButtonConfigComposer extends MessageComposer {
    private final String badge;
    private final boolean enabled;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.isBadgeRequestFulfilledComposer);
        this.response.appendString(this.badge);
        this.response.appendBoolean(this.enabled);
        return this.response;
    }
}