package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitManager;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;

import java.util.List;

public abstract class RoomUnitSubManager {

    protected RoomUnitManager roomUnitManager;
    protected Room room;
    public RoomUnitSubManager(RoomUnitManager roomUnitManager){
        this.roomUnitManager = roomUnitManager;
        this.room = roomUnitManager.getRoom();
    }

    public abstract List<? extends RoomUnit> cycle();
}
