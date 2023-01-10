package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.HabboClubOffersMessageComposer;

public class GetClubOffersEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new HabboClubOffersMessageComposer(this.client.getHabbo(), this.packet.readInt()));
    }
}
