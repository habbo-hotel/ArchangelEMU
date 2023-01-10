package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildMembershipsMessageComposer;
import gnu.trove.set.hash.THashSet;

public class GetGuildMembershipsEvent extends MessageHandler {
    @Override
    public void handle() {
        THashSet<Guild> guilds = new THashSet<>();

        for (int i : this.client.getHabbo().getHabboStats().getGuilds()) {
            if (i == 0)
                continue;

            Guild g = Emulator.getGameEnvironment().getGuildManager().getGuild(i);

            if (g != null) {
                guilds.add(g);
            }
        }

        this.client.sendResponse(new GuildMembershipsMessageComposer(guilds, this.client.getHabbo()));
    }
}
