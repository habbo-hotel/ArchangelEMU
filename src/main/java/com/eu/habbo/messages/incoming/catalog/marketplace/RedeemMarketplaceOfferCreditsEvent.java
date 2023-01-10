package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RedeemMarketplaceOfferCreditsEvent extends MessageHandler {
    @Override
    public void handle() {
        MarketPlace.getCredits(this.client);
    }
}
