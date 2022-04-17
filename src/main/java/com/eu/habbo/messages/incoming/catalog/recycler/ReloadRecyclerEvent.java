package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RecyclerStatusComposer;

public class ReloadRecyclerEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new RecyclerStatusComposer());
    }
}
