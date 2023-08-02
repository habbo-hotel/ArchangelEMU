package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import lombok.Getter;

@Getter
public class RoomBot extends RoomAvatar {
    private Bot unit;

    public RoomBot() {
        super();
    }

    @Override
    public void cycle() {
        super.cycle();
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.BOT;
    }
}
