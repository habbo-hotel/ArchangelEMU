package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitManager;

public abstract class RoomUnitSubManager {

    protected RoomUnitManager roomUnitManager;
    protected Room room;
    public RoomUnitSubManager(RoomUnitManager roomUnitManager){
        this.roomUnitManager = roomUnitManager;
        this.room = roomUnitManager.getRoom();
    }
}
