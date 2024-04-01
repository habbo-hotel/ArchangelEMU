package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.police.ArrestCommand;

public class PoliceArrestEvent extends MessageHandler {
    @Override
    public void handle() {
        String targetedUsername = this.packet.readString();

        if (targetedUsername == null) {
            return;
        }

        new ArrestCommand().handle(this.client,new String[] {null, targetedUsername});
    }
}