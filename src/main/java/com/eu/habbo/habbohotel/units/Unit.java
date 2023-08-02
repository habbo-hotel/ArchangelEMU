package com.eu.habbo.habbohotel.units;

import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Unit {
    private RoomUnit roomUnit;
}