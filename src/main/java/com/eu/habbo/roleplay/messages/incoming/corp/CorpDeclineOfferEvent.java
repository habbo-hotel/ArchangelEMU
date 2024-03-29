package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.corporation.CorpDeclineJobCommand;

public class CorpDeclineOfferEvent  extends MessageHandler {
    @Override
    public void handle() {
        String corpName = this.packet.readString();

        if (corpName == null) {
            return;
        }

        new CorpDeclineJobCommand().handle(this.client, new String[] {null, corpName});
    }
}