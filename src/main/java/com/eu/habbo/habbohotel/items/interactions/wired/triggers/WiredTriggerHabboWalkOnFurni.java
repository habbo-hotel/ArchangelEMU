package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboWalkOnFurni extends InteractionWiredTrigger {
    public WiredTriggerHabboWalkOnFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerHabboWalkOnFurni(int id, int userId, Item item, String extraData, int limitedStack, int limitedSells) {
        super(id, userId, item, extraData, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(stuff.length == 0) {
            return false;
        }

        if (stuff[0] instanceof HabboItem) {
            return this.getWiredSettings().getItems(room).contains(stuff[0]);
        }

        return false;
    }

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.WALKS_ON_FURNI;
    }
}
