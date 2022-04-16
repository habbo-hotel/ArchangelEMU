package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceItemStatsComposer;

public class RequestItemInfoEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.packet.readInt();
        int id = this.packet.readInt();

        this.client.sendResponse(new MarketplaceItemStatsComposer(id));
    }
}
