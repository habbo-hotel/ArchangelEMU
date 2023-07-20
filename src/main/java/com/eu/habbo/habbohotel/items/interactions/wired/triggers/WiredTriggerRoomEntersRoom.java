package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerRoomEntersRoom extends InteractionWiredTrigger {
    public WiredTriggerRoomEntersRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerRoomEntersRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getStringParam().isEmpty()) {
            return true;
        }

        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

        if (habbo != null) {
            return habbo.getHabboInfo().getUsername().equalsIgnoreCase(this.getWiredSettings().getStringParam());
        }

        return false;
    }

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.ENTER_ROOM;
    }
}