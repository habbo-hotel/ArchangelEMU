package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.police.PoliceListReportsComposer;

public class PoliceListReportsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new PoliceListReportsComposer(this.client.getHabbo()));
    }
}