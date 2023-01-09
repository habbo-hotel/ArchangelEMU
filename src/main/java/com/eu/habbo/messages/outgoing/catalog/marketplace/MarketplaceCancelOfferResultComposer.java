package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MarketplaceCancelOfferResultComposer extends MessageComposer {
    private final MarketPlaceOffer offer;
    private final boolean success;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.marketplaceCancelOfferResultComposer);
        this.response.appendInt(this.offer.getOfferId());
        this.response.appendBoolean(this.success);
        return this.response;
    }
}
