package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolRoomVisit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;

import java.util.Calendar;
import java.util.TimeZone;

@AllArgsConstructor
public class RoomVisitsComposer extends MessageComposer {
    private final HabboInfo habboInfo;
    private final THashSet<ModToolRoomVisit> roomVisits;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomVisitsComposer);
        this.response.appendInt(this.habboInfo.getId());
        this.response.appendString(this.habboInfo.getUsername());
        this.response.appendInt(this.roomVisits.size());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        for (ModToolRoomVisit visit : this.roomVisits) {
            cal.setTimeInMillis(visit.getTimestamp() * 1000L);
            this.response.appendInt(visit.getRoomId());
            this.response.appendString(visit.getRoomName());
            this.response.appendInt(cal.get(Calendar.HOUR));
            this.response.appendInt(cal.get(Calendar.MINUTE));
        }
        return this.response;
    }
}
