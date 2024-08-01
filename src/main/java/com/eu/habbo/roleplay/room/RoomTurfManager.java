package com.eu.habbo.roleplay.room;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RoomTurfManager {

    public static int TWO_MINUTES_IN_SECONDS = 120;

    private Habbo capturingHabbo;

    @Setter
    private int secondsLeft;

    @Setter
    private int captureProgress;

    private boolean capturing;

    public void startCapturing(Habbo capturingHabbo) {
        this.capturingHabbo = capturingHabbo;
        this.secondsLeft = TWO_MINUTES_IN_SECONDS - this.captureProgress;
        this.capturing = true;
    }

    public void stopCapturing() {
        this.capturingHabbo = null;
        this.captureProgress = TWO_MINUTES_IN_SECONDS - this.secondsLeft;
        this.secondsLeft = -1;
        this.capturing = false;
    }

    public void pauseCapturing() {
        this.capturing = false;
    }

    public void resumeCapturing() {
        this.capturing = true;
    }

    public void decrementSecondsLeft() {
        if (this.capturing && this.secondsLeft > 0) {
            this.secondsLeft--;
            this.captureProgress = TWO_MINUTES_IN_SECONDS - this.secondsLeft;
        }
    }

    public void regainTime() {
        if (this.captureProgress > 0) {
            this.captureProgress--;
            if (this.secondsLeft < TWO_MINUTES_IN_SECONDS) {
                this.secondsLeft++;
            }
        }
    }
}