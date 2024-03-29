package com.eu.habbo.roleplay.messages.incoming.corp;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.corporation.CorpStartWorkCommand;

public class CorpStartWorkEvent extends MessageHandler {
    @Override
    public void handle() {
        new CorpStartWorkCommand().handle(this.client, new String[] {});
    }
}