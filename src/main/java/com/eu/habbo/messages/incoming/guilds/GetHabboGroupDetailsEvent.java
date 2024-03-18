package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.HabboGroupDetailsMessageComposer;

public class GetHabboGroupDetailsEvent extends MessageHandler {
    @Override
    public void handle() {
        int guildId = this.packet.readInt();
        boolean newWindow = this.packet.readBoolean();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild != null) {
            this.client.sendResponse(new HabboGroupDetailsMessageComposer(guild, this.client, newWindow, Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, this.client.getHabbo())));
        }
    }
}
