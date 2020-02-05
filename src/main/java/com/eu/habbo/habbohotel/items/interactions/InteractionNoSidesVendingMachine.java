package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionNoSidesVendingMachine extends InteractionVendingMachine {
    public InteractionNoSidesVendingMachine(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionNoSidesVendingMachine(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public RoomTile getRequiredTile(Habbo habbo, Room room) {
        return habbo.getRoomUnit().getClosestAdjacentTile(this.getX(), this.getY(), true);
    }
}
