package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import lombok.Getter;

@Getter
public class RoomBot extends RoomAvatar {
    public RoomBot() {
        super();
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.BOT;
    }
}
