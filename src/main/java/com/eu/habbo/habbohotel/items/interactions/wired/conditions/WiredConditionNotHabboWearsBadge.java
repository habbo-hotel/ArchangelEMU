package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotHabboWearsBadge extends WiredConditionHabboWearsBadge {
    public static final WiredConditionType type = WiredConditionType.NOT_ACTOR_WEARS_BADGE;

    protected String badge = "";

    public WiredConditionNotHabboWearsBadge(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionNotHabboWearsBadge(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return !super.execute(roomUnit, room, stuff);
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.NOT_ACTOR_WEARS_BADGE;
    }
}
