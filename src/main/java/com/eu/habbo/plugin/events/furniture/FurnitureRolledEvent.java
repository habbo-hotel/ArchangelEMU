package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class FurnitureRolledEvent extends FurnitureEvent {

    public final RoomItem roller;


    public final RoomTile newLocation;


    public FurnitureRolledEvent(RoomItem furniture, RoomItem roller, RoomTile newLocation) {
        super(furniture);

        this.roller = roller;
        this.newLocation = newLocation;
    }
}
