package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildEditorDataMessageComposer;

public class GetGuildEditorDataEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new GuildEditorDataMessageComposer());
    }
}
