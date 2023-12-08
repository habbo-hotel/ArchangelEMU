package com.eu.habbo.habbohotel.rooms.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomRotation {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    private final int direction;


    public static RoomRotation fromValue(int rotation) {
        rotation %= 8;
        for (RoomRotation rot : values()) {
            if (rot.getValue() == rotation) {
                return rot;
            }
        }

        return NORTH;
    }

    public static RoomRotation counterClockwise(RoomRotation rotation) {
        return fromValue(rotation.getValue() + 7);
    }

    public static RoomRotation clockwise(RoomRotation rotation) {
        return fromValue(rotation.getValue() + 9);
    }

    public int getValue() {
        return this.direction;
    }

    public RoomRotation getOpposite() {
        return switch (this) {
            case NORTH -> RoomRotation.SOUTH;
            case NORTH_EAST -> RoomRotation.SOUTH_WEST;
            case EAST -> RoomRotation.WEST;
            case SOUTH_EAST -> RoomRotation.NORTH_WEST;
            case SOUTH -> RoomRotation.NORTH;
            case SOUTH_WEST -> RoomRotation.NORTH_EAST;
            case WEST -> RoomRotation.EAST;
            case NORTH_WEST -> RoomRotation.SOUTH_EAST;
        };
    }
}
