package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.police.WantedListComposer;

public class WantedListEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new WantedListComposer());
    }
}