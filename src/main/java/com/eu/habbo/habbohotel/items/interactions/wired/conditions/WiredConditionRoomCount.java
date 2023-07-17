package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionRoomCount extends InteractionWiredCondition {
    public final int PARAM_LOWER_LIMIT = 0;
    public final int PARAM_UPPER_LIMIT = 1;

    public WiredConditionRoomCount(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionRoomCount(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int lowerLimit = this.getWiredSettings().getIntegerParams().get(PARAM_LOWER_LIMIT);
        int upperLimit = this.getWiredSettings().getIntegerParams().get(PARAM_UPPER_LIMIT);
        int userCount = room.getRoomUnitManager().getRoomHabbosCount();

        return userCount >= lowerLimit && userCount <= upperLimit;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.USER_COUNT;
    }
}
