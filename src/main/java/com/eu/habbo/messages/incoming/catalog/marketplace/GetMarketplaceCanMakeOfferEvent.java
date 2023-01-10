package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceCanMakeOfferResult;

public class GetMarketplaceCanMakeOfferEvent extends MessageHandler {
    @Override
    public void handle() {
        if (MarketPlace.MARKETPLACE_ENABLED)
            this.client.sendResponse(new MarketplaceCanMakeOfferResult(1, 0, 0));
        else
            this.client.sendResponse(new MarketplaceCanMakeOfferResult(3, 0, 0));
    }
}
