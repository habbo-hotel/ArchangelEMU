package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrSendKickbackInfoMessageComposer extends MessageComposer {
    private final int currentHcStreak;
    private final String firstSubDate;
    private final double kickbackPercentage;
    private final int totalCreditsMissed;
    private final int totalCreditsRewarded;
    private final int totalCreditsSpent;
    private final int creditRewardForStreakBonus;
    private final int creditRewardForMonthlySpent;
    private final int timeUntilPayday;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.scrSendKickbackInfoMessageComposer);
        this.response.appendInt(this.currentHcStreak); // currentHcStreak (days)
        this.response.appendString(this.firstSubDate); // firstSubscriptionDate (dd-mm-yyyy)
        this.response.appendDouble(this.kickbackPercentage); // kickbackPercentage (e.g. 0.1 for 10%)
        this.response.appendInt(this.totalCreditsMissed); // (not used)
        this.response.appendInt(this.totalCreditsRewarded); // (not used)
        this.response.appendInt(this.totalCreditsSpent);
        this.response.appendInt(this.creditRewardForStreakBonus);
        this.response.appendInt(this.creditRewardForMonthlySpent);
        this.response.appendInt(this.timeUntilPayday); // timeUntilPayday (minutes)
        return this.response;
    }
}