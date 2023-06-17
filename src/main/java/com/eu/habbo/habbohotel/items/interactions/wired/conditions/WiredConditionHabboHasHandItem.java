package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class WiredConditionHabboHasHandItem extends InteractionWiredCondition {
    public final int PARAM_HAND_ITEM_ID = 0;

    public WiredConditionHabboHasHandItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionHabboHasHandItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (roomUnit == null) {
            return false;
        }

        int handItemId = this.getWiredSettings().getIntegerParams().get(PARAM_HAND_ITEM_ID);

        return roomUnit.getHandItem() == handItemId;
    }

    @Override
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.ACTOR_HAS_HANDITEM;
    }
}
