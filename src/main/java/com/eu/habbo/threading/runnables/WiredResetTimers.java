package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WiredResetTimers implements Runnable {
    private final Room room;

    @Override
    public void run() {
        if (!Emulator.isShuttingDown && Emulator.isReady) {
            WiredHandler.resetTimers(this.room);
        }
    }
}
