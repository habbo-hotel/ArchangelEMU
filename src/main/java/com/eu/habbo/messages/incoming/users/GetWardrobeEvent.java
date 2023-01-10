package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.WardrobeMessageComposer;

public class GetWardrobeEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new WardrobeMessageComposer(this.client.getHabbo().getInventory().getWardrobeComponent()));
    }
}
