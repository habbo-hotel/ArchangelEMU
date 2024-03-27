package com.eu.habbo.roleplay.messages.incoming;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.corporation.CorpStopWorkCommand;

public class CorpStopWorkEvent  extends MessageHandler {
    @Override
    public void handle() {
        new CorpStopWorkCommand().handle(this.client, new String[] {});
    }
}