package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.hotelview.BonusRareInfoMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveHotelviewBonusRarePoints extends WiredEffectWhisper {
    public WiredEffectGiveHotelviewBonusRarePoints(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveHotelviewBonusRarePoints(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
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

        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

        if (habbo == null) {
            return false;
        }

        if (amount > 0) {
            habbo.givePoints(Emulator.getConfig().getInt("hotelview.promotional.points.type"), amount);
            habbo.getClient().sendResponse(new BonusRareInfoMessageComposer(habbo));
        }

        return true;
    }
}
