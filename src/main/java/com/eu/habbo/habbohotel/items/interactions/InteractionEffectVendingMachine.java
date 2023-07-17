package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionEffectVendingMachine extends InteractionVendingMachine {
    public InteractionEffectVendingMachine(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionEffectVendingMachine(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public void giveVendingMachineItem(Room room, RoomAvatar roomAvatar) {
        room.giveEffect(roomAvatar, this.getBaseItem().getRandomVendingItem(), 30);
    }
}