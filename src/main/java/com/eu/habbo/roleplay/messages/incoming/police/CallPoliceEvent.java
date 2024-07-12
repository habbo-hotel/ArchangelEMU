package com.eu.habbo.roleplay.messages.incoming.police;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.police.CallPoliceCommand;

public class CallPoliceEvent extends MessageHandler {
    @Override
    public void handle() {
        String message = this.packet.readString();

        if (message == null) {
            return;
        }

        new CallPoliceCommand().handle(this.client,new String[] {null, message});
    }
}