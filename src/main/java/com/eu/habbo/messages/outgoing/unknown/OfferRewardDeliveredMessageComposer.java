package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OfferRewardDeliveredMessageComposer extends MessageComposer {
    private final Item item;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.offerRewardDeliveredMessageComposer);
        this.response.appendString(this.item.getType().code);
        this.response.appendInt(this.item.getId());
        this.response.appendString(this.item.getName());
        this.response.appendString(this.item.getFullName());
        return this.response;
    }
}