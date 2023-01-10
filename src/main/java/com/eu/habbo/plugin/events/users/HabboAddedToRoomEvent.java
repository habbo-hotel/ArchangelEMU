package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

import java.util.Collection;

@Getter
public class HabboAddedToRoomEvent extends UserEvent {

    private final Room room;
    private final Collection<Habbo> habbosToSendEnter;
    private final Collection<Habbo> visibleHabbos;

    public HabboAddedToRoomEvent(Habbo habbo, Room room, Collection<Habbo> habbosToSendEnter, Collection<Habbo> visibleHabbos) {
        super(habbo);

        this.room = room;
        this.habbosToSendEnter = habbosToSendEnter;
        this.visibleHabbos = visibleHabbos;
    }
}
