package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class QuestCancelledMessageComposer extends MessageComposer {
    private final boolean expired;

    public QuestCancelledMessageComposer(boolean expired) {
        this.expired = expired;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.questCancelledMessageComposer);
        this.response.appendBoolean(this.expired);
        return this.response;
    }
}