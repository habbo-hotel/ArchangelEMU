package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerFurniStateToggled extends InteractionWiredTrigger {
    public WiredTriggerFurniStateToggled(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerFurniStateToggled(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(stuff.length == 0 || room.getRoomUnitManager().getHabboByRoomUnit(roomUnit) == null) {
            return false;
        }

        if (stuff[0] instanceof RoomItem && !(stuff[0] instanceof WiredEffectType)) {
            return this.getWiredSettings().getItems(room).contains(stuff[0]);
        }

        return false;
    }

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.STATE_CHANGED;
    }
}
