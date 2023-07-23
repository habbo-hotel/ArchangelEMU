package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionTriggerOnFurni extends InteractionWiredCondition {
    public WiredConditionTriggerOnFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionTriggerOnFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        if(roomUnit == null) {
            return false;
        }

        THashSet<RoomItem> itemsAtUser = room.getRoomItemManager().getItemsAt(roomUnit.getCurrentPosition());
        return this.getWiredSettings().getItems(room).stream().anyMatch(itemsAtUser::contains);
    }

    @Override
    public WiredConditionOperator operator() {
        return WiredConditionOperator.AND;
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.TRIGGER_ON_FURNI;
    }
}
