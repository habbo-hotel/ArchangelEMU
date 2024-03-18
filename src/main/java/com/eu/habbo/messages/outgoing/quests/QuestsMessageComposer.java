package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class QuestsMessageComposer extends MessageComposer {
    private final List<Quest> quests;
    private final boolean unknownBoolean;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.questsMessageComposer);
        this.response.appendInt(this.quests.size());
        for (Quest quest : this.quests) {
            this.response.append(quest);
        }
        this.response.appendBoolean(this.unknownBoolean);
        return this.response;
    }

    @AllArgsConstructor
    public static class Quest implements ISerialize {
        private final String campaignCode;
        private final int completedQuestsInCampaign;
        private final int questCountInCampaign;
        private final int activityPointType;
        private final int id;
        private final boolean accepted;
        private final String type;
        private final String imageVersion;
        private final int rewardCurrencyAmount;
        private final String localizationCode;
        private final int completedSteps;
        private final int totalSteps;
        private final int sortOrder;
        private final String catalogPageName;
        private final String chainCode;
        private final boolean easy;


        @Override
        public void serialize(ServerMessage message) {
            message.appendString(this.campaignCode);
            message.appendInt(this.completedQuestsInCampaign);
            message.appendInt(this.questCountInCampaign);
            message.appendInt(this.activityPointType);
            message.appendInt(this.id);
            message.appendBoolean(this.accepted);
            message.appendString(this.type);
            message.appendString(this.imageVersion);
            message.appendInt(this.rewardCurrencyAmount);
            message.appendString(this.localizationCode);
            message.appendInt(this.completedSteps);
            message.appendInt(this.totalSteps);
            message.appendInt(this.sortOrder);
            message.appendString(this.catalogPageName);
            message.appendString(this.chainCode);
            message.appendBoolean(this.easy);
        }
    }
}