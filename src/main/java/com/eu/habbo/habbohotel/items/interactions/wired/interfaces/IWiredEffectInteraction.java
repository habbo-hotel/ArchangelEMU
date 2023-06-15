package com.eu.habbo.habbohotel.items.interactions.wired.interfaces;

import com.eu.habbo.habbohotel.rooms.Room;

import java.util.List;

public interface IWiredEffectInteraction {
    List<Integer> getBlockedTriggers(Room room);
    void setBlockedTriggers(List<Integer> value);
}
