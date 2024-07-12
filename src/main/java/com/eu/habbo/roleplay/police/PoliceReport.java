package com.eu.habbo.roleplay.police;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class PoliceReport {
    @Getter
    public final Habbo reportingUser;
    @Getter
    public final Room reportedRoom;
    @Getter
    public final String message;
    @Getter
    @Setter
    public Habbo respondingOfficer;
    @Getter
    @Setter
    public boolean resolved;
}
