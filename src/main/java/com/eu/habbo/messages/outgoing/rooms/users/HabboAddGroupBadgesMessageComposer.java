package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboAddGroupBadgesMessageComposer extends MessageComposer {
    private final Guild guild;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboGroupBadgesMessageComposer);
        this.response.appendInt(1);
        this.response.appendInt(this.guild.getId());
        this.response.appendString(this.guild.getBadge());
        return this.response;
    }
}