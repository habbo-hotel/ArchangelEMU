package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Collection;

public class HabboAddedToRoomEvent extends UserEvent {

    public final Room room;
    public final Collection<Habbo> habbosToSendEnter;


    public HabboAddedToRoomEvent(Habbo habbo, Room room, Collection<Habbo> habbosToSendEnter) {
        super(habbo);

        this.room = room;
        this.habbosToSendEnter = habbosToSendEnter;
    }
}
