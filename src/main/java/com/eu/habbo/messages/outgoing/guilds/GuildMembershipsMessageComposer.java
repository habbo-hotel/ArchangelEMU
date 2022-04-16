package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class GuildMembershipsMessageComposer extends MessageComposer {
    private final THashSet<Guild> guilds;
    private final Habbo habbo;

    public GuildMembershipsMessageComposer(THashSet<Guild> guilds, Habbo habbo) {
        this.guilds = guilds;
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.GuildMembershipsMessageComposer);
        this.response.appendInt(this.guilds.size());
        for (Guild guild : this.guilds) {
            this.response.appendInt(guild.getId());
            this.response.appendString(guild.getName());
            this.response.appendString(guild.getBadge());
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorOne()).valueA);
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getBackgroundColor(guild.getColorTwo()).valueA);
            this.response.appendBoolean(this.habbo.getHabboStats().guild == guild.getId());
            this.response.appendInt(guild.getOwnerId());
            this.response.appendBoolean(guild.hasForum());
        }
        return this.response;
    }
}
