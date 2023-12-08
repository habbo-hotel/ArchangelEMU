package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotFurniTypeMatch extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.NOT_STUFF_IS;
    private final THashSet<RoomItem> items = new THashSet<>();

    public WiredConditionNotFurniTypeMatch(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionNotFurniTypeMatch(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        if(stuff.length == 0) {
            return true;
        }

        if (stuff[0] instanceof RoomItem triggeringItem) {
            return this.getWiredSettings().getItems(room).stream().noneMatch(item -> item == triggeringItem);
        }

        return false;
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.NOT_STUFF_IS;
    }
}
