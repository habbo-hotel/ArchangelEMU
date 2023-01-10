package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class SupportRoomActionEvent extends SupportEvent {

    private final Room room;
    private final boolean kickUsers;
    private final boolean lockDoor;
    private final boolean changeTitle;


    public SupportRoomActionEvent(Habbo moderator, Room room, boolean kickUsers, boolean lockDoor, boolean changeTitle) {
        super(moderator);

        this.room = room;
        this.kickUsers = kickUsers;
        this.lockDoor = lockDoor;
        this.changeTitle = changeTitle;
    }
}