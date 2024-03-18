package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HotelViewCatalogPageExpiringComposer extends MessageComposer {
    private final String name;
    private final int time;
    private final String image;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.hotelViewCatalogPageExpiringComposer);
        this.response.appendString(this.name);
        this.response.appendInt(this.time);
        this.response.appendString(this.image);
        return this.response;
    }
}