package com.eu.habbo.habbohotel.rooms.entities;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Deque;

@Getter
@Setter
@Accessors(chain = true)
public abstract class RoomEntity implements IRoomEntity {
    protected Room room;
    protected RoomTile previousPosition;
    protected double previousZ;
    protected RoomTile currentPosition;
    protected double currentZ;
    protected RoomTile nextPosition;
    protected double nextZ;
    protected RoomTile targetPosition;
    protected double targetZ;
    protected Deque<RoomTile> path;
    protected int tilesMoved;

    public boolean isAtGoal() {
        if(this.targetPosition == null) {
            return true;
        }

        return this.currentPosition.equals(this.targetPosition);
    }

    public RoomEntity setCurrentPosition(RoomTile tile) {
        this.previousPosition = this.currentPosition;
        this.currentPosition = tile;
        return this;
    }

    public RoomEntity setCurrentZ(double currentZ) {
        this.previousZ = this.currentZ;
        this.currentZ = Math.max(-9999, Math.min(currentZ, 9999));
        return this;
    }

    protected void clearNextLocation() {
        this.nextPosition = null;
        this.nextZ = 0;
    }

    public synchronized void incrementTilesMoved() {
        this.tilesMoved++;
    }

    public synchronized void decrementTilesMoved() {
        this.tilesMoved--;
    }

    public void clear() {
        this.room = null;

        this.previousPosition = null;
        this.previousZ = 0;

        this.currentPosition = null;
        this.currentZ = 0;

        this.nextPosition = null;
        this.nextZ = 0;

        this.targetPosition = null;
        this.targetZ = 0;

        this.path = null;
    }
}