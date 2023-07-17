package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;

public abstract class FurnitureUserEvent extends FurnitureEvent {

    public final Habbo habbo;


    public FurnitureUserEvent(RoomItem furniture, Habbo habbo) {
        super(furniture);
        this.habbo = habbo;
    }
}
