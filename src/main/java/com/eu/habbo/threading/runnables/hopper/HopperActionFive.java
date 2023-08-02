package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class HopperActionFive implements Runnable {
    private final RoomItem currentTeleport;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        this.client.getHabbo().getRoomUnit().setTeleporting(false);
        RoomTile tile = this.room.getLayout().getTileInFront(this.room.getLayout().getTile(this.currentTeleport.getCurrentPosition().getX(), this.currentTeleport.getCurrentPosition().getY()), this.currentTeleport.getRotation());
        if (tile != null) {
            this.client.getHabbo().getRoomUnit().walkTo(tile);
        }

        Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, this.room, "0"), 1000);
    }
}
