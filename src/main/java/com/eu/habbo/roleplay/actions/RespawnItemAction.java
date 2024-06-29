package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;

import java.util.Collection;

public class RespawnItemAction implements Runnable {

    private final Collection<RoomUnit> roomUnits;

    public RespawnItemAction(Collection<RoomUnit> roomUnits) {
        this.roomUnits = roomUnits;
        for (RoomUnit unit : this.roomUnits) {
            unit.setInvisible(true);
        }
        Emulator.getThreading().run(this, 1000 * 5);
    }
    @Override
    public void run() {
        for (RoomUnit unit : this.roomUnits) {
            unit.setInvisible(false);
        }
    }


}
