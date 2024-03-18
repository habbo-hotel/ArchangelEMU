package com.eu.habbo.plugin.events.users.calendar;

import com.eu.habbo.habbohotel.campaign.CalendarCampaign;
import com.eu.habbo.habbohotel.campaign.CalendarRewardObject;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;
import lombok.Getter;

@Getter
public class UserClaimRewardEvent extends UserEvent {

    private final CalendarCampaign campaign;
    private final int day;
    private final CalendarRewardObject reward;
    private final boolean force;

    public UserClaimRewardEvent(Habbo habbo, CalendarCampaign campaign, int day, CalendarRewardObject reward, boolean force) {
        super(habbo);

        this.campaign = campaign;
        this.day = day;
        this.reward = reward;
        this.force = force;
    }
}
