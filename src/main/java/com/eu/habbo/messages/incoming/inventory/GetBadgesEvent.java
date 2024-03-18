package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.BadgesComposer;

public class GetBadgesEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BadgesComposer(this.client.getHabbo()));
    }
}
