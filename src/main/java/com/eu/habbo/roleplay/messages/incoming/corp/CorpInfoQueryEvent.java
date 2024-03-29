package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpInfoComposer;

public class CorpInfoQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        Integer corpID = this.packet.readInt();

        if (corpID == null) {
            return;
        }

        this.client.sendResponse(new CorpInfoComposer(corpID));
    }
}