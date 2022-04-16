package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.BotInventoryComposer;

public class RequestInventoryBotsEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new BotInventoryComposer(this.client.getHabbo()));
    }
}
