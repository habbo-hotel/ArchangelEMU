package com.eu.habbo.roleplay.messages.incoming.guild;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.GuildEditorDataMessageComposer;

public class GetGuildEditorDataEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new GuildEditorDataMessageComposer());
    }
}
