package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHanditemTile extends InteractionHanditem {
    public InteractionHanditemTile(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionHanditemTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        InteractionHanditemTile instance = this;
        Emulator.getThreading().run(() -> {
            if (roomUnit.getCurrentLocation().getX() == instance.getX() && roomUnit.getCurrentLocation().getY() == instance.getY()) {
                instance.handle(room, roomUnit);
            }
        }, 3000);
    }
}