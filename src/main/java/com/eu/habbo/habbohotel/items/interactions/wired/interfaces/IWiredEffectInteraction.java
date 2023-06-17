package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.util.List;

public interface IWiredEffectInteraction {
    WiredEffectType getType();
    void setType(WiredEffectType value);
    List<Integer> getBlockedTriggers(Room room);
    void setBlockedTriggers(List<Integer> value);
}
