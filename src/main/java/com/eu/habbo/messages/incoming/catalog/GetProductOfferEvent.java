package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.ProductOfferComposer;
import gnu.trove.iterator.TIntObjectIterator;

public class GetProductOfferEvent extends MessageHandler {
    @Override
    public void handle() {
        int offerId = this.packet.readInt();

        int pageId = Emulator.getGameEnvironment().getCatalogManager().offerDefs.get(offerId);

        if (pageId != 0) {
            CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().getCatalogPage(Emulator.getGameEnvironment().getCatalogManager().getCatalogItem(pageId).getPageId());

            if (page != null) {
                TIntObjectIterator<CatalogItem> iterator = page.getCatalogItems().iterator();

                while (iterator.hasNext()) {
                    iterator.advance();

                    CatalogItem item = iterator.value();

                    if (item.getOfferId() == offerId) {
                        this.client.sendResponse(new ProductOfferComposer(item));
                        return;
                    }
                }
            }
        }
    }
}
