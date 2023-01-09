package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomState {
    OPEN(0),
    LOCKED(1),
    PASSWORD(2),
    INVISIBLE(3);

    private final int state;


}
