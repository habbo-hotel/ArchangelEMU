package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.corp.CorpDemoteCommand;

public class CorpDemoteUserEvent extends MessageHandler {
    @Override
    public void handle() {
        String targetedUsername = this.packet.readString();

        if (targetedUsername == null) {
            return;
        }

        new CorpDemoteCommand().handle(this.client, new String[] {null, targetedUsername});
    }
}