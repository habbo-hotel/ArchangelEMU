package com.eu.habbo.messages.incoming.guild;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guild.GuildEditInfoMessageComposer;

public class GetGuildEditInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.packet.readInt());

        this.client.sendResponse(new GuildEditInfoMessageComposer(guild));
    }
}
