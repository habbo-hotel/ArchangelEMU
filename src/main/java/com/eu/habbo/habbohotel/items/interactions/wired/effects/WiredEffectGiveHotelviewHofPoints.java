package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveHotelviewHofPoints extends InteractionWiredEffect {
    public WiredEffectGiveHotelviewHofPoints(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveHotelviewHofPoints(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getStringParam().isEmpty()) {
            return false;
        }

        int amount;

        try {
            amount = Integer.parseInt(this.getWiredSettings().getStringParam());
        } catch (Exception e) {
            return false;
        }

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo == null) {
            return false;
        }

        if (amount > 0) {
            habbo.getHabboStats().hofPoints += amount;
            Emulator.getThreading().run(habbo.getHabboStats());
        }

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.SHOW_MESSAGE;
    }
}
