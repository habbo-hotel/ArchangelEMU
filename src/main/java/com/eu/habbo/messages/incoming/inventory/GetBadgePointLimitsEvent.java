package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.BadgePointLimitsComposer;

public class GetBadgePointLimitsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BadgePointLimitsComposer());
    }
}
