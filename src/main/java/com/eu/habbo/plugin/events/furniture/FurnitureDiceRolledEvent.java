package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import lombok.Getter;

@Getter
public class FurnitureDiceRolledEvent extends FurnitureUserEvent {

    private final int result;


    public FurnitureDiceRolledEvent(RoomItem furniture, Habbo habbo, int result) {
        super(furniture, habbo);

        this.result = result;
    }
}
