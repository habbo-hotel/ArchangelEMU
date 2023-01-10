package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketPlaceOwnOffersComposer;

public class GetMarketplaceOwnOffersEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new MarketPlaceOwnOffersComposer(this.client.getHabbo()));
    }
}
