package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;

public class GuildForumDataEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null)
            return;

        this.client.sendResponse(new GuildForumDataComposer(guild, this.client.getHabbo()));
    }
}