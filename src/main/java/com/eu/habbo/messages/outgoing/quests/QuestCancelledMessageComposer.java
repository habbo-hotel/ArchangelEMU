package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuestCancelledMessageComposer extends MessageComposer {
    private final boolean expired;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.questCancelledMessageComposer);
        this.response.appendBoolean(this.expired);
        return this.response;
    }
}