package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuildFurniContextMenuInfoMessageComposer extends MessageComposer {
    private final Habbo habbo;
    private final Guild guild;
    private final HabboItem item;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guildFurniContextMenuInfoMessageComposer);
        this.response.appendInt(item.getId());
        this.response.appendInt(this.guild.getId());
        this.response.appendString(this.guild.getName());
        this.response.appendInt(this.guild.getRoomId());
        this.response.appendBoolean(Emulator.getGameEnvironment().getGuildManager().getGuildMember(this.guild, this.habbo) != null); //User Joined.
        this.response.appendBoolean(this.guild.hasForum()); //Has Forum.
        return this.response;
    }
}
