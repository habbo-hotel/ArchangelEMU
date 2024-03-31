package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.corp.CorpOfferUserJobCommand;

public class CorpOfferUserJobEvent extends MessageHandler {
    @Override
    public void handle() {
        String targetedUsername = this.packet.readString();

        if (targetedUsername == null) {
            return;
        }

        new CorpOfferUserJobCommand().handle(this.client, new String[] {null, targetedUsername});
    }
}