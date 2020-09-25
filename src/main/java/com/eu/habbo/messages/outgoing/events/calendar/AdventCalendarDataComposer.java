package com.eu.habbo.messages.outgoing.events.calendar;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;

public class AdventCalendarDataComposer extends MessageComposer {
    private final String eventName;
    private final int totalDays;
    private final int currentDay;
    private final TIntArrayList unlocked;
    private final boolean lockExpired;

    public AdventCalendarDataComposer(String eventName, int totalDays, int currentDay, TIntArrayList unlocked, boolean lockExpired) {
        this.eventName = eventName;
        this.totalDays = totalDays;
        this.currentDay = currentDay;
        this.unlocked = unlocked;
        this.lockExpired = lockExpired;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.AdventCalendarDataComposer);
        this.response.appendString(this.eventName);
        this.response.appendString("");
        this.response.appendInt(this.currentDay);
        this.response.appendInt(this.totalDays);
        this.response.appendInt(this.unlocked.size());

        TIntArrayList expired = new TIntArrayList();
        for (int i = 0; i < this.totalDays; i++) {
            expired.add(i);
        }
        expired.remove(this.currentDay);
        if(this.currentDay > 1) expired.remove(this.currentDay - 2);
        if(this.currentDay > 0) expired.remove(this.currentDay - 1);

        this.unlocked.forEach(value -> {
            AdventCalendarDataComposer.this.response.appendInt(value);
            expired.remove(value);
            return true;
        });


        if (this.lockExpired) {
            this.response.appendInt(expired.size());
            expired.forEach(value -> {
                AdventCalendarDataComposer.this.response.appendInt(value);
                return true;
            });
        } else {
            this.response.appendInt(0);
        }

        return this.response;
    }
}