package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuildMemberFurniCountInHQMessageComposer extends MessageComposer {
    private final int userId;
    private final int furniCount;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guildMemberFurniCountInHQMessageComposer);
        this.response.appendInt(this.userId);
        this.response.appendInt(this.furniCount);
        return this.response;
    }
}
