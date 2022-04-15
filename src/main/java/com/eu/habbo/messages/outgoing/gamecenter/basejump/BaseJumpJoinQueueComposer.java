package com.eu.habbo.messages.outgoing.gamecenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BaseJumpJoinQueueComposer extends MessageComposer {
    private final int gameId;

    public BaseJumpJoinQueueComposer(int gameId) {
        this.gameId = gameId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.JoinedQueueMessageComposer);
        this.response.appendInt(this.gameId);
        return this.response;
    }
}