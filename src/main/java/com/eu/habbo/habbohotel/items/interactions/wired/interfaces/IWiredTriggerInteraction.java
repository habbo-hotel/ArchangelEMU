package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.rooms.Room;

import java.util.List;

public interface IWiredTriggerInteraction {
    List<Integer> getBlockedEffects(Room room);
    void setBlockedEffects(List<Integer> value);
}
