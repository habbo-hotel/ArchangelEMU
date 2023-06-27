package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.rooms.RoomTile;

public interface IWiredPeriodical {
    int getInterval();
    void setInterval(int value);
    boolean isTriggerTileUpdated();
    void setTriggerTileUpdated(boolean value);
    RoomTile getOldTile();
    void setOldTile(RoomTile value);
}
