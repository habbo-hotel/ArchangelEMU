package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LimitedOfferAppearingNextMessageComposer extends MessageComposer {
    private final int time;
    private final int pageId;
    private final int itemId;
    private final String itemName;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.limitedOfferAppearingNextMessageComposer);
        this.response.appendInt(this.time);
        this.response.appendInt(this.pageId);
        this.response.appendInt(this.itemId);
        this.response.appendString(this.itemName);
        return this.response;
    }
}