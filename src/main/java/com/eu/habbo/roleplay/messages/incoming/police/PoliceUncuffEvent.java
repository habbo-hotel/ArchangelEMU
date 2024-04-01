package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.police.UncuffCommand;

public class PoliceUncuffEvent extends MessageHandler {
    @Override
    public void handle() {
        String targetedUsername = this.packet.readString();

        if (targetedUsername == null) {
            return;
        }

        new UncuffCommand().handle(this.client,new String[] {null, targetedUsername});
    }
}