package com.eu.habbo.roleplay.messages.incoming.gang;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.gang.GangInviteAcceptCommand;

public class GangInviteAcceptEvent extends MessageHandler {
    @Override
    public void handle() {
        String targetedUsername = this.packet.readString();

        if (targetedUsername == null) {
            return;
        }

        new GangInviteAcceptCommand().handle(this.client, new String[] {null, targetedUsername});
    }
}