package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.PetInventoryComposer;

public class GetPetInventoryEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new PetInventoryComposer(this.client.getHabbo()));
    }
}
