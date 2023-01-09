package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MarketplaceItemStatsComposer extends MessageComposer {
    private final int itemId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.marketplaceItemStatsComposer);
        MarketPlace.serializeItemInfo(this.itemId, this.response);
        return this.response;
    }
}
