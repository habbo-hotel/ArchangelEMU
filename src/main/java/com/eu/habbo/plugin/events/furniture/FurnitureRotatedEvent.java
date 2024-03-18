package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class FurnitureRotatedEvent extends FurnitureUserEvent {

    public final int oldRotation;


    public FurnitureRotatedEvent(RoomItem furniture, Habbo habbo, int oldRotation) {
        super(furniture, habbo);

        this.oldRotation = oldRotation;
    }
}
