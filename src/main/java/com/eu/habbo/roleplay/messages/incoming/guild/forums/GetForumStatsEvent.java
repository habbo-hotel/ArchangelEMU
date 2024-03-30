package com.eu.habbo.roleplay.messages.incoming.guild.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.forums.ForumDataMessageComposer;

public class GetForumStatsEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null)
            return;

        this.client.sendResponse(new ForumDataMessageComposer(guild, this.client.getHabbo()));

        if (!Emulator.getGameEnvironment().getGuildManager().hasViewedForum(this.client.getHabbo().getHabboInfo().getId(), guildId)) {
            Emulator.getGameEnvironment().getGuildManager().addView(this.client.getHabbo().getHabboInfo().getId(), guildId);
        }
    }
}