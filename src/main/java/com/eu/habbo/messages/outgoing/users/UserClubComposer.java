package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.time.Period;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserClubComposer extends MessageComposer {
    private final Habbo habbo;

    public UserClubComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.UserClubComposer);

        this.response.appendString("club_habbo");

        Subscription subscription = this.habbo.getHabboStats().getSubscription(Subscription.HABBO_CLUB);

        int days = 0;
        int minutes = 0;
        int timeRemaining = 0;

        if(subscription != null) {
            timeRemaining = subscription.getRemaining();
            days = (int) Math.floor(timeRemaining / 86400.0);
            minutes = (int) Math.ceil(timeRemaining / 60.0);

            if(days < 1 && minutes > 0) {
                days = 1;
            }
        }

        this.response.appendInt(days); // daysToPeriodEnd
        this.response.appendInt(0); // memberPeriods
        this.response.appendInt(0); // periodsSubscribedAhead
        this.response.appendInt(0); // responseType
        this.response.appendBoolean(true); // hasEverBeenMember
        this.response.appendBoolean(true); // isVIP
        this.response.appendInt(0); // pastClubDays
        this.response.appendInt(0); // pastVIPdays
        this.response.appendInt(minutes); // minutesTillExpiration
        this.response.appendInt((Emulator.getIntUnixTimestamp() - this.habbo.getHabboStats().hcMessageLastModified) / 60); // minutesSinceLastModified
        this.habbo.getHabboStats().hcMessageLastModified = Emulator.getIntUnixTimestamp();

        // int - daysToPeriodEnd
        // int - memberPeriods
        // int - periodsSubscribedAhead
        // int - responseType
        // bool - hasEverBeenMember
        // bool - isVIP
        // int - pastClubDays
        // int - pastVIPdays
        // int - minutesTillExpiration
        // (optional) int - minutesSinceLastModified


        /*
        int endTimestamp = this.habbo.getHabboStats().getClubExpireTimestamp();
        int now = Emulator.getIntUnixTimestamp();

        if (endTimestamp >= now) {


            int days = ((endTimestamp - Emulator.getIntUnixTimestamp()) / 86400);
            int years = (int) Math.floor(days / 365);

            //if(years > 0)


            int months = 0;

            if (days > 31) {
                months = (int) Math.floor(days / 31);
                days = days - (months * 31);
            }

            this.response.appendInt(days);
            this.response.appendInt(1);
            this.response.appendInt(months);
            this.response.appendInt(years);
        } else {
            this.response.appendInt(0);
            this.response.appendInt(7);
            this.response.appendInt(0);
            this.response.appendInt(1);
        }

        this.response.appendBoolean(true);
        this.response.appendBoolean(true);
        this.response.appendInt(0);
        this.response.appendInt(0);

        long remaining = (endTimestamp - Emulator.getIntUnixTimestamp()) * 1000;

        if (remaining > Integer.MAX_VALUE || remaining <= 0) {
            this.response.appendInt(Integer.MAX_VALUE);
        } else {
            this.response.appendInt((int) remaining);
        }
        */

        return this.response;
    }
}
