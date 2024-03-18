package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommunityGoalVoteMessageComposer extends MessageComposer {
    private final boolean unknownBoolean;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.communityGoalVoteMessageComposer);
        this.response.appendBoolean(this.unknownBoolean);
        return this.response;
    }
}