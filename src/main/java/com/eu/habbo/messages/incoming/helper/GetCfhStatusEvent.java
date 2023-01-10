package com.eu.habbo.messages.incoming.helper;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.SanctionStatusComposer;

public class GetCfhStatusEvent extends MessageHandler {

    @Override
    public void handle() {
        this.client.sendResponse(new SanctionStatusComposer(this.client.getHabbo()));
    }
}
