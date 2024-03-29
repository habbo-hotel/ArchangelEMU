package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpPositionInfoComposer;

public class CorpPositionInfoQueryEvent  extends MessageHandler {
    @Override
    public void handle() {
        Integer corpID = this.packet.readInt();
        Integer corpPositionID = this.packet.readInt();

        if (corpID == null || corpPositionID == null) {
            return;
        }

        this.client.sendResponse(new CorpPositionInfoComposer(corpID, corpPositionID));
    }
}