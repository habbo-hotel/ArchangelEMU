package com.eu.habbo.roleplay.room;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class RoomTurfManager {

    public static int TWO_MINUTES_IN_SECONDS = 120;

    private Habbo capturingHabbo;
    @Setter
    private boolean blocked;

    @Setter
    private int secondsLeft;

    public void startCapturing(Habbo capturingHabbo) {
        this.blocked = false;
        this.capturingHabbo = capturingHabbo;
        this.secondsLeft = RoomTurfManager.TWO_MINUTES_IN_SECONDS;
    }

    public void stopCapturing() {
        this.blocked = false;
        this.capturingHabbo = null;
        this.secondsLeft = -1;
    }

}