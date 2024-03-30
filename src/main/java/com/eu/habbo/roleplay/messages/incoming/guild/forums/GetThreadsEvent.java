package com.eu.habbo.roleplay.messages.incoming.guild.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.forums.ForumDataMessageComposer;
import com.eu.habbo.roleplay.messages.outgoing.guild.forums.GuildForumThreadsComposer;
import com.eu.habbo.messages.outgoing.handshake.ErrorReportComposer;

public class GetThreadsEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = packet.readInt();
        int index = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null) {
            this.client.sendResponse(new ErrorReportComposer(404));
            return;
        }

        this.client.sendResponse(new ForumDataMessageComposer(guild, this.client.getHabbo()));
        this.client.sendResponse(new GuildForumThreadsComposer(guild, index));
    }
}