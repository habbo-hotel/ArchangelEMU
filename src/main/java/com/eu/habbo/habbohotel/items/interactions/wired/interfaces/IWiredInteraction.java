package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
public interface IWiredInteraction {
    WiredSettings getWiredSettings();
    void setWiredSettings(WiredSettings value);
    boolean execute(RoomUnit roomUnit, Room room, Object[] stuff);
}
