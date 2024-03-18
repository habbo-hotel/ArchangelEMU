package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomRatingComposer extends MessageComposer {
    private final int score;
    private final boolean canVote;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomRatingComposer);
        this.response.appendInt(this.score);
        this.response.appendBoolean(this.canVote);
        return this.response;
    }
}
