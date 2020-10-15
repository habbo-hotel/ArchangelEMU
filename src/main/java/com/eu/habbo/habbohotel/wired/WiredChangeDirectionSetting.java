package com.eu.habbo.habbohotel.wired;

import com.eu.habbo.habbohotel.rooms.RoomUserRotation;

public class WiredChangeDirectionSetting {
    public final int itemId;
    public int rotation;
    public RoomUserRotation direction;

    public WiredChangeDirectionSetting(int itemId, int rotation, RoomUserRotation direction) {
        this.itemId = itemId;
        this.rotation = rotation;
        this.direction = direction;
    }
}
