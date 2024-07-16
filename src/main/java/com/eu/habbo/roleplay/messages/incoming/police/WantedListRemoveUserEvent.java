package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.police.WantedListRemoveUserCommand;

public class WantedListRemoveUserEvent extends MessageHandler {
    @Override
    public void handle() {
        String username = this.packet.readString();
        if (username == null) {
            return;
        }
        new WantedListRemoveUserCommand().handle(this.client,new String[] {null, username});
    }
}