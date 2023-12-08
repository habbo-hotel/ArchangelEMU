package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.plugin.Event;

public abstract class FurnitureEvent extends Event {

    public final RoomItem furniture;


    public FurnitureEvent(RoomItem furniture) {
        this.furniture = furniture;
    }
}
