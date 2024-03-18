package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class UserRolledEvent extends UserEvent {

    public final RoomItem roller;


    public final RoomTile location;


    public UserRolledEvent(Habbo habbo, RoomItem roller, RoomTile location) {
        super(habbo);

        this.roller = roller;
        this.location = location;
    }
}
