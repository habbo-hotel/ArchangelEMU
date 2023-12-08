package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomState {
    OPEN,
    LOCKED,
    PASSWORD,
    INVISIBLE
}
