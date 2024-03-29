package com.eu.habbo.roleplay.messages.incoming.gang;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.gang.GangInfoComposer;

public class GangInfoQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        Integer gangID = this.packet.readInt();

        if (gangID == null) {
            return;
        }

        this.client.sendResponse(new GangInfoComposer(gangID));
    }
}