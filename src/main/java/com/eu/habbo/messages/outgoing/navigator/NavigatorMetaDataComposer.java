package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NavigatorMetaDataComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.navigatorMetaDataComposer);
        this.response.appendInt(4);
        this.response.appendString("official_view");
        this.response.appendInt(0);
        this.response.appendString("hotel_view");
        this.response.appendInt(0);
        this.response.appendString("roomads_view");
        this.response.appendInt(0);
        this.response.appendString("myworld_view");
        this.response.appendInt(0);

        return this.response;
    }
}
