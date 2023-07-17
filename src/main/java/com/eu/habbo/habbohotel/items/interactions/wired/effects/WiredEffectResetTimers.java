package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.threading.runnables.WiredResetTimers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectResetTimers extends InteractionWiredEffect {
    public WiredEffectResetTimers(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectResetTimers(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Emulator.getThreading().run(new WiredResetTimers(room), this.getWiredSettings().getDelay());
        return true;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.RESET_TIMERS;
    }
}
