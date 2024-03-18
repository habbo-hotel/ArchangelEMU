package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuildEditFailedMessageComposer extends MessageComposer {
    public static final int ROOM_ALREADY_IN_USE = 0;
    public static final int INVALID_GUILD_NAME = 1;
    public static final int HC_REQUIRED = 2;
    public static final int MAX_GUILDS_JOINED = 3;

    private final int errorCode;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guildEditFailedMessageComposer);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}
