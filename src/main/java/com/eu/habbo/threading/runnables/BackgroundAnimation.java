package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BackgroundAnimation implements Runnable {
    private final HabboItem toner;
    private final Room room;
    private int length = 1000;
    private int state = 0;


    @Override
    public void run() {
        if (this.room.isLoaded() && !this.room.isPreLoaded()) {
            this.toner.setExtradata("1:" + this.state + ":126:126");
            this.state = (this.state + 1) % 256;
            this.room.updateItem(this.toner);

            if (this.toner.getRoomId() > 0 && this.length > 0) {
                Emulator.getThreading().run(this, 500);
                this.length--;
            }
        }
    }
}
