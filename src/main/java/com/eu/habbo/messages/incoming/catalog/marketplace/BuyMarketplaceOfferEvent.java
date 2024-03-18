package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.incoming.MessageHandler;

public class BuyMarketplaceOfferEvent extends MessageHandler {
    @Override
    public void handle() {
        int offerId = this.packet.readInt();

        MarketPlace.buyItem(offerId, this.client);
    }
}
