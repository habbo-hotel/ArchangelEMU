package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveEffect extends WiredEffectWhisper {
    public WiredEffectGiveEffect(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveEffect(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getStringParam().isEmpty()) {
            return false;
        }

        int effectId;

        try {
            effectId = Integer.parseInt(this.getWiredSettings().getStringParam());
        } catch (Exception e) {
            return false;
        }

        if(roomUnit instanceof RoomAvatar roomAvatar) {
            if (effectId >= 0) {
                roomAvatar.giveEffect(effectId, Integer.MAX_VALUE);
                return true;
            }
        }

        return false;
    }
}