package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RecyclerPrizesComposer;

public class RequestRecyclerLogicEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new RecyclerPrizesComposer());
    }
}
