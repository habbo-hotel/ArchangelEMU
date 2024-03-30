package com.eu.habbo.roleplay.messages.incoming.guild;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.guild.GuildEditInfoMessageComposer;

public class GetGuildEditInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.packet.readInt());

        this.client.sendResponse(new GuildEditInfoMessageComposer(guild));
    }
}
