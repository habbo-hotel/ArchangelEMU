package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.guilds.Guild;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GangInfoComposer extends MessageComposer {
    private final int gangID;

    @Override
    protected ServerMessage composeInternal() {
        Guild matchingGang = Emulator.getGameEnvironment().getGuildManager().getGuild(this.gangID);
        this.response.init(Outgoing.gangInfoComposer);
        this.response.appendInt(matchingGang.getId());;
        this.response.appendInt(matchingGang.getOwnerId());;
        this.response.appendInt(matchingGang.getRoomId());
        this.response.appendString(matchingGang.getName());
        this.response.appendString(matchingGang.getDescription());
        this.response.appendString(matchingGang.getBadge());
        return this.response;
    }
}
