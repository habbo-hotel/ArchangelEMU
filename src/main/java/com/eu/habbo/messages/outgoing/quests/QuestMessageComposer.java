package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class QuestMessageComposer extends MessageComposer {
    private final QuestsMessageComposer.Quest quest;

    public QuestMessageComposer(QuestsMessageComposer.Quest quest) {
        this.quest = quest;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.QuestMessageComposer);
        this.response.append(this.quest);
        return this.response;
    }
}