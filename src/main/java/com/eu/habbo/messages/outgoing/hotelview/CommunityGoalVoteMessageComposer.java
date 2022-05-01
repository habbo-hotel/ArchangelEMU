package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CommunityGoalVoteMessageComposer extends MessageComposer {
    private final boolean unknownBoolean;

    public CommunityGoalVoteMessageComposer(boolean unknownBoolean) {
        this.unknownBoolean = unknownBoolean;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.communityGoalVoteMessageComposer);
        this.response.appendBoolean(this.unknownBoolean);
        return this.response;
    }
}