package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SeasonalCalendarDailyOfferMessageComposer extends MessageComposer {
    private final int pageId;
    private final CatalogItem catalogItem;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.seasonalCalendarDailyOfferMessageComposer);
        this.response.appendInt(this.pageId);
        this.catalogItem.serialize(this.response);
        return this.response;
    }
}