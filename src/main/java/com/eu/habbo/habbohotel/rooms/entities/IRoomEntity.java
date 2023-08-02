package com.eu.habbo.habbohotel.rooms.entities;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;

public interface IRoomEntity {
    Room getRoom();
    RoomEntity setRoom(Room room);

    RoomTile getPreviousPosition();
    RoomEntity setPreviousPosition(RoomTile previousPosition);

    double getPreviousZ();
    RoomEntity setPreviousZ(double previousZ);

    RoomTile getCurrentPosition();
    RoomEntity setCurrentPosition(RoomTile currentPosition);

    double getCurrentZ();
    RoomEntity setCurrentZ(double currentZ);

    RoomTile getNextPosition();
    RoomEntity setNextPosition(RoomTile nextPosition);

    double getNextZ();
    RoomEntity setNextZ(double nextZ);

    RoomTile getTargetPosition();
    RoomEntity setTargetPosition(RoomTile targetPosition);

    double getTargetZ();
    RoomEntity setTargetZ(double targetZ);

    void incrementTilesMoved();
}