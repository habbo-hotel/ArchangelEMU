package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.plugin.Event;

public class FurnitureStackHeightEvent extends Event {

    public final short x;
    public final short y;
    public final Room room;
    private boolean pluginHelper;

    public FurnitureStackHeightEvent(short x, short y, Room room) {
        this.x = x;
        this.y = y;
        this.room = room;
        this.pluginHelper = false;
    }

    public void setPluginHelper(boolean helper) {
        this.pluginHelper = helper;
    }

    public boolean hasPluginHelper() {
        return this.pluginHelper;
    }
}
