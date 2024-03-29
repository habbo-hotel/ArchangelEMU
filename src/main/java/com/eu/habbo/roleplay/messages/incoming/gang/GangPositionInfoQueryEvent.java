package com.eu.habbo.roleplay.messages.incoming.gang;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.gang.GangPositionInfoComposer;

public class GangPositionInfoQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        Integer gangID = this.packet.readInt();
        Integer gangPositionID = this.packet.readInt();

        if (gangID == null || gangPositionID == null) {
            return;
        }

        this.client.sendResponse(new GangPositionInfoComposer(gangID, gangPositionID));
    }
}