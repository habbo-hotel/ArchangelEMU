package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuildMembershipUpdatedMessageComposer extends MessageComposer {
    private final Guild guild;
    private final GuildMember guildMember;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guildMembershipUpdatedMessageComposer);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.guildMember.getRank().getType());
        this.response.appendInt(this.guildMember.getUserId());
        this.response.appendString(this.guildMember.getUsername());
        this.response.appendString(this.guildMember.getLook());
        this.response.appendString(this.guildMember.getRank().getType() != 0 ? this.guildMember.getJoinDate() + "" : "");
        return this.response;
    }
}
