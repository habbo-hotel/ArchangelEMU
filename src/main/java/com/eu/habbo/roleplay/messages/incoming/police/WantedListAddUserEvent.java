package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.police.WantedListAddUserCommand;

public class WantedListAddUserEvent extends MessageHandler {
    @Override
    public void handle() {
        String username =this.packet.readString();
        String crime = this.packet.readString();

        if (username == null || crime == null) {
            return;
        }
        new WantedListAddUserCommand().handle(this.client,new String[] {null, username, crime});
    }
}