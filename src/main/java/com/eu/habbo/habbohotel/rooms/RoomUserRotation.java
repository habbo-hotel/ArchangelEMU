package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomUserRotation {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    private final int direction;


    public static RoomUserRotation fromValue(int rotation) {
        rotation %= 8;
        for (RoomUserRotation rot : values()) {
            if (rot.getValue() == rotation) {
                return rot;
            }
        }

        return NORTH;
    }

    public static RoomUserRotation counterClockwise(RoomUserRotation rotation) {
        return fromValue(rotation.getValue() + 7);
    }

    public static RoomUserRotation clockwise(RoomUserRotation rotation) {
        return fromValue(rotation.getValue() + 9);
    }

    public int getValue() {
        return this.direction;
    }

    public RoomUserRotation getOpposite() {
        return switch (this) {
            case NORTH -> RoomUserRotation.SOUTH;
            case NORTH_EAST -> RoomUserRotation.SOUTH_WEST;
            case EAST -> RoomUserRotation.WEST;
            case SOUTH_EAST -> RoomUserRotation.NORTH_WEST;
            case SOUTH -> RoomUserRotation.NORTH;
            case SOUTH_WEST -> RoomUserRotation.NORTH_EAST;
            case WEST -> RoomUserRotation.EAST;
            case NORTH_WEST -> RoomUserRotation.SOUTH_EAST;
        };
    }
}
