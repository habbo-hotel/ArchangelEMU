package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.BotInventoryComposer;

public class GetBotInventoryEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BotInventoryComposer(this.client.getHabbo()));
    }
}
