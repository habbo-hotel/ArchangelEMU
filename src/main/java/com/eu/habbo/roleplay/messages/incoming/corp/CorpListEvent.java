package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpListComposer;

public class CorpListEvent extends MessageHandler {

    @Override
    public void handle() {
        this.client.sendResponse(new CorpListComposer());
    }
}