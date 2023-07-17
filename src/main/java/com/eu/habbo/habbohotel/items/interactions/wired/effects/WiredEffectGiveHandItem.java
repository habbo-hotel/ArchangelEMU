package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveHandItem extends WiredEffectWhisper {
    public WiredEffectGiveHandItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveHandItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getStringParam().isEmpty()) {
            return false;
        }

        try {
            int itemId = Integer.parseInt(this.getWiredSettings().getStringParam());

            Habbo habbo = room.getHabbo(roomUnit);

            if (habbo != null) {
                habbo.getRoomUnit().setHandItem(itemId);
                room.sendComposer(new CarryObjectMessageComposer(habbo.getRoomUnit()).compose());
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
