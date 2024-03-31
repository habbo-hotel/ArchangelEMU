package com.eu.habbo.roleplay.corp;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class CorporationShift {

    public static long SHIFT_LENGTH_IN_MS = (5 * (60 * 1000));

    private Habbo habbo;

    private String oldLook;

    private long startTime;

    private long endTime;

    public CorporationShift(Habbo habbo) {
        this.habbo = habbo;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + CorporationShift.SHIFT_LENGTH_IN_MS;
        this.oldLook = habbo.getHabboInfo().getLook();
    }
}