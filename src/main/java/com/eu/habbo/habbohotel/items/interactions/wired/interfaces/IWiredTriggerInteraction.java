package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.util.List;

public interface IWiredTriggerInteraction {
    WiredTriggerType getType();
    void setType(WiredTriggerType value);
    List<Integer> getBlockedEffects(Room room);
    void setBlockedEffects(List<Integer> value);
}
