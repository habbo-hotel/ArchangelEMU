package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomUnitType {
    USER(1),
    BOT(4),
    PET(2),
    UNKNOWN(3);

    private final int typeId;

}
