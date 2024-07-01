package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

public class RoomTile implements Comparable<RoomTile> {
    @Getter
    private final short x;
    @Getter
    private final short y;
    @Getter
    private final short z;
    private final HashSet<RoomUnit> roomUnits;
    private final HashSet<RoomItem> roomItems;
    @Setter
    @Getter
    private RoomTileState state;
    @Getter
    private double stackHeight;
    private boolean allowStack = true;
    @Getter
    @Setter
    private RoomTile previous = null;
    private boolean diagonally;
    @Getter
    private double gCosts;
    private double hCosts;


    public double getGCosts() {
        return gCosts;
    }

    public void setgCosts(RoomTile fromTile) {
        this.gCosts = fromTile.getGCosts() + calculateMovementCost(fromTile);
    }

    public double getHCosts() {
        return hCosts;
    }

    public void sethCosts(RoomTile targetTile) {
        this.hCosts = calculateHeuristicCost(targetTile);
    }

    public double getFCost() {
        return this.gCosts + this.hCosts;
    }

    public double distanceTo(RoomTile other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    private double calculateMovementCost(RoomTile fromTile) {
        // Implement the logic to calculate the actual movement cost from 'fromTile' to this tile
        // This might include the Euclidean distance, Manhattan distance, or any domain-specific cost
        return 1.0; // Placeholder value, replace with actual calculation
    }

    private double calculateHeuristicCost(RoomTile targetTile) {
        // Implement the heuristic cost calculation (e.g., Euclidean distance, Manhattan distance)
        return Math.abs(this.getX() - targetTile.getX()) + Math.abs(this.getY() - targetTile.getY());
    }


    public RoomTile(short x, short y, short z, RoomTileState state, boolean allowStack) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stackHeight = z;
        this.state = state;
        this.setAllowStack(allowStack);
        this.roomUnits = new HashSet<>();
        this.roomItems = new HashSet<>();
    }

    public RoomTile(RoomTile tile) {
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.z;
        this.stackHeight = tile.stackHeight;
        this.state = tile.state;
        this.allowStack = tile.allowStack;
        this.diagonally = tile.diagonally;
        this.gCosts = tile.gCosts;
        this.hCosts = tile.hCosts;

        if (this.state == RoomTileState.INVALID) {
            this.allowStack = false;
        }

        this.roomUnits = tile.roomUnits;
        this.roomItems = tile.roomItems;
    }

    @Override
    public int compareTo(RoomTile other) {
        // Implement comparison logic here
        // For example, compare based on F cost
        return Double.compare(this.getFCost(), other.getFCost());
    }

    public void setStackHeight(double stackHeight) {
        if (this.state == RoomTileState.INVALID) {
            this.stackHeight = Short.MAX_VALUE;
            this.allowStack = false;
            return;
        }

        if (stackHeight >= 0 && stackHeight != Short.MAX_VALUE) {
            this.stackHeight = stackHeight;
            this.allowStack = true;
        } else {
            this.allowStack = false;
            this.stackHeight = this.z;
        }
    }

    public boolean getAllowStack() {
        if (this.state == RoomTileState.INVALID) {
            return false;
        }

        return this.allowStack;
    }

    public void setAllowStack(boolean allowStack) {
        this.allowStack = allowStack;
    }

    public short relativeHeight() {
        if (this.state == RoomTileState.INVALID) {
            return Short.MAX_VALUE;
        } else if (!this.allowStack && (this.state == RoomTileState.BLOCKED || this.state == RoomTileState.SIT)) {
            return 64 * 256;
        }

        return this.allowStack ? (short) (this.getStackHeight() * 256.0) : 64 * 256;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RoomTile &&
                ((RoomTile) o).x == this.x &&
                ((RoomTile) o).y == this.y;
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public RoomTile copy() {
        return new RoomTile(this);
    }

    public double distance(RoomTile roomTile) {
        double x = (double) this.x - roomTile.x;
        double y = (double) this.y - roomTile.y;
        return Math.sqrt(x * x + y * y);
    }

    public void isDiagonally(boolean isDiagonally) {
        this.diagonally = isDiagonally;
    }

    public String toString() {
        return "RoomTile (" + this.x + ", " + this.y + ", " + this.z + "): h: " + this.hCosts + " g: " + this.gCosts + " f: " + this.getFCost();
    }

    public boolean isWalkable() {
        return this.state == RoomTileState.OPEN;
    }

    public boolean is(short x, short y) {
        return this.x == x && this.y == y;
    }

    public void addRoomUnit(RoomUnit roomUnit) {
        synchronized (this.roomUnits) {
            this.roomUnits.add(roomUnit);
        }
    }

    public void removeUnit(RoomUnit roomUnit) {
        synchronized (this.roomUnits) {
            if(!this.roomUnits.contains(roomUnit)) {
                return;
            }

            this.roomUnits.remove(roomUnit);

            if(roomUnit instanceof RoomAvatar roomAvatar && roomAvatar.isRiding()) {
                this.roomUnits.remove(roomAvatar.getRidingPet().getRoomUnit());
            }
        }
    }

    //TODO Move this to RoomUnit
    public boolean unitIsOnFurniOnTile(RoomUnit roomUnit, Item item) {
        if ((roomUnit.getCurrentPosition().getX() < this.x || roomUnit.getCurrentPosition().getX() >= this.x + item.getLength())) {
            return false;
        }

        if (roomUnit.getCurrentPosition().getY() < this.y) {
            return false;
        }

        return roomUnit.getCurrentPosition().getY() < this.y + item.getWidth();
    }

}