package com.eu.habbo.messages.outgoing.guild;

import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuildMembershipRejectedMessageComposer extends MessageComposer {
    private final Guild guild;
    private final Integer userId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guildMembershipRejectedMessageComposer);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.userId);
        return this.response;
    }
}
