package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoadCustomHeightMap implements Runnable {
    private final Room room;
    @Override
    public void run() {
        this.room.setLayout(Emulator.getGameEnvironment().getRoomManager().loadCustomLayout(this.room));
    }
}
