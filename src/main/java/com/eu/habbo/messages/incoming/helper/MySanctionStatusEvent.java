package com.eu.habbo.messages.incoming.helper;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.SanctionStatusComposer;

public class MySanctionStatusEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new SanctionStatusComposer(this.client.getHabbo()));
    }
}
