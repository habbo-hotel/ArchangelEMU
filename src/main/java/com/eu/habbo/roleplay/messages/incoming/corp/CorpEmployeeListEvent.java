package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.messages.outgoing.corp.CorpEmployeeListComposer;

public class CorpEmployeeListEvent extends MessageHandler {

    @Override
    public void handle() {
        int corpID = this.packet.readInt();

        Corp corp = CorpManager.getInstance().getCorpByID(corpID);

        if (corp == null) {
            return;
        }

        this.client.sendResponse(new CorpEmployeeListComposer(corp.getGuild().getId()));
    }
}