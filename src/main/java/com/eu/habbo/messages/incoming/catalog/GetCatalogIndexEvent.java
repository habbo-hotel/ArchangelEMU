package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.BuildersClubFurniCountMessageComposer;
import com.eu.habbo.messages.outgoing.catalog.CatalogPagesListComposer;

public class GetCatalogIndexEvent extends MessageHandler {
    @Override
    public void handle() {

        String MODE = this.packet.readString();
        if (MODE.equalsIgnoreCase("normal")) {
            this.client.sendResponse(new BuildersClubFurniCountMessageComposer(0));
            this.client.sendResponse(new CatalogPagesListComposer(this.client.getHabbo(), MODE));
        } else {
            this.client.sendResponse(new BuildersClubFurniCountMessageComposer(1));
            this.client.sendResponse(new CatalogPagesListComposer(this.client.getHabbo(), MODE));
        }

    }
}
