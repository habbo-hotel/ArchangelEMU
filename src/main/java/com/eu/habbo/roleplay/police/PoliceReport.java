package com.eu.habbo.roleplay.police;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PoliceReport {
    public final Habbo reportingUser;
    public final Room reportedRoom;
    public final String message;
    @Setter
    public Habbo respondingOfficer;
    @Setter
    public boolean resolved;
    @Setter
    public boolean flagged;
}
