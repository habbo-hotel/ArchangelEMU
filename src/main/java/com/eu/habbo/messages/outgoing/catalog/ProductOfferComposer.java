package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProductOfferComposer extends MessageComposer {
    private final CatalogItem item;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.productOfferComposer);
        this.item.serialize(this.response);
        return this.response;
    }
}
