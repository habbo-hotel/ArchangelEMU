package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildEditInfoMessageComposer;

public class GetGuildEditInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.packet.readInt());

        this.client.sendResponse(new GuildEditInfoMessageComposer(guild));
    }
}
