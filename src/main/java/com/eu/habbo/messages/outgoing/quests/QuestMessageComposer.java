package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuestMessageComposer extends MessageComposer {
    private final QuestsMessageComposer.Quest quest;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.questMessageComposer);
        this.response.append(this.quest);
        return this.response;
    }
}