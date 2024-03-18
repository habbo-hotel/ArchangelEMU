package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RecyclerPrizesComposer;

public class GetRecyclerPrizesEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new RecyclerPrizesComposer());
    }
}
