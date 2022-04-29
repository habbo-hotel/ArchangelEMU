package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildMembershipRejectedMessageComposer extends MessageComposer {
    private final Guild guild;
    private final Integer userId;

    public GuildMembershipRejectedMessageComposer(Guild guild, int userId) {
        this.guild = guild;
        this.userId = userId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.GuildMembershipRejectedMessageComposer);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.userId);
        return this.response;
    }
}
