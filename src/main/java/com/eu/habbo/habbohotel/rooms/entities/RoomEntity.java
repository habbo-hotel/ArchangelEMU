package com.eu.habbo.habbohotel.rooms.entities;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Deque;
import java.util.LinkedList;

@Getter
@Setter
@Accessors(chain = true)
public abstract class RoomEntity implements IRoomEntity {
    private Room room;
    private RoomTile previousPosition;
    private double previousZ;
    private RoomTile currentPosition;
    private double currentZ;
    private RoomTile targetPosition;
    private double targetZ;
    private Deque<RoomTile> path = new LinkedList<>();
    private int tilesMoved;

    public boolean isAtGoal() {
        if(this.currentPosition == null) {
            return false;
        }

        return this.currentPosition.equals(this.targetPosition);
    }

    public void setPreviousLocation(RoomTile previousLocation) {
        this.previousPosition = previousLocation;
        this.previousZ = this.getCurrentZ();
    }

    public RoomEntity setCurrentZ(double currentZ) {
        this.previousZ = this.currentZ;
        this.currentZ = Math.max(-9999, Math.min(currentZ, 9999));
        return this;
    }

    public synchronized void incrementTilesMoved() {
        this.tilesMoved++;
    }

    public synchronized void decrementTilesMoved() {
        this.tilesMoved--;
    }
}