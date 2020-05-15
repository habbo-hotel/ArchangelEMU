package com.eu.habbo.messages.outgoing.events.calendar;

import com.eu.habbo.habbohotel.catalog.CalendarRewardObject;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AdventCalendarProductComposer extends MessageComposer {
    public final boolean visible;
    public final CalendarRewardObject rewardObject;

    public AdventCalendarProductComposer(boolean visible, CalendarRewardObject rewardObject) {
        this.visible = visible;
        this.rewardObject = rewardObject;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.AdventCalendarProductComposer);
        this.response.appendBoolean(this.visible);
        this.response.appendString(this.rewardObject.getItem().getName());
        this.response.appendString(this.rewardObject.getCustomImage());
        this.response.appendString(this.rewardObject.getItem().getName());
        return this.response;
    }
}