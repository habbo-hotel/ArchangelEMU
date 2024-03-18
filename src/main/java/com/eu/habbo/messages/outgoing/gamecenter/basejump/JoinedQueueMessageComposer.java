package com.eu.habbo.messages.outgoing.gamecenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JoinedQueueMessageComposer extends MessageComposer {
    private final int gameId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.joinedQueueMessageComposer);
        this.response.appendInt(this.gameId);
        return this.response;
    }
}