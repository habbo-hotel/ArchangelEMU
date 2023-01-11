package com.eu.habbo.plugin.events.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class NavigatorRoomsEvent extends UserEvent {

    public final List<Room> rooms;


    public NavigatorRoomsEvent(Habbo habbo, List<Room> rooms) {
        super(habbo);

        this.rooms = rooms;
    }
}
