package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RecyclerStatusComposer;

public class GetRecyclerStatusEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new RecyclerStatusComposer());
    }
}
