package com.eu.habbo.roleplay.messages.incoming.gang;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.gang.GangDisbandCommand;

public class GangDisbandEvent extends MessageHandler {
    @Override
    public void handle() {
        new GangDisbandCommand().handle(this.client, new String[] {});
    }
}