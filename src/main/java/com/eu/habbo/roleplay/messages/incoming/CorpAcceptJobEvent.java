package com.eu.habbo.roleplay.messages.incoming;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.corporation.CorpAcceptJobCommand;

public class CorpAcceptJobEvent extends MessageHandler {
    @Override
    public void handle() {
        String corpName = this.packet.readString();

        if (corpName == null) {
            return;
        }

        new CorpAcceptJobCommand().handle(this.client, new String[] {null, corpName});
    }
}