package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class HopperActionFour implements Runnable {
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        this.currentTeleport.setExtradata("1");
        this.room.updateItem(this.currentTeleport);

        Emulator.getThreading().run(new HopperActionFive(this.currentTeleport, this.room, this.client), 500);
    }
}
