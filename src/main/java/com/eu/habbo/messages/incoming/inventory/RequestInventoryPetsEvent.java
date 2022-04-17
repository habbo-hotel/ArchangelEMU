package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.PetInventoryComposer;

public class RequestInventoryPetsEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new PetInventoryComposer(this.client.getHabbo()));
    }
}
