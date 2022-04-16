package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildMembershipRejectedMessageComposer extends MessageComposer {
    private final Guild guild;

    public GuildMembershipRejectedMessageComposer(Guild guild) {
        this.guild = guild;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.GuildMembershipRejectedMessageComposer);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(0);
        return this.response;
    }
}
