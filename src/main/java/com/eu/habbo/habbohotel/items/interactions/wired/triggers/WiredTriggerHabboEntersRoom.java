package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboEntersRoom extends InteractionWiredTrigger {
    public final int PARAM_ANY_USER = 0;
    public WiredTriggerHabboEntersRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerHabboEntersRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        boolean anyUser = this.getWiredSettings().getIntegerParams().get(PARAM_ANY_USER) == 0;

        if(this.getWiredSettings().getStringParam().isEmpty() && !anyUser || anyUser) {
            return true;
        }

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            return habbo.getHabboInfo().getUsername().equalsIgnoreCase(this.getWiredSettings().getStringParam());
        }

        return false;
    }

    @Override
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().size() == 0) {
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.ENTER_ROOM;
    }
}
