package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboSaysKeyword extends InteractionWiredTrigger {
    private static final WiredTriggerType type = WiredTriggerType.SAY_SOMETHING;

    private static int PARAM_OWNER_ONLY = 0;

    public WiredTriggerHabboSaysKeyword(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerHabboSaysKeyword(int id, int userId, Item item, String extraData, int limitedStack, int limitedSells) {
        super(id, userId, item, extraData, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (this.getWiredSettings().getStringParam().isEmpty()) {
            return false;
        }

        boolean ownerOnly = this.getWiredSettings().getIntegerParams()[PARAM_OWNER_ONLY] == 1;

        if (stuff[0] instanceof String) {
            if (((String) stuff[0]).toLowerCase().contains(this.getWiredSettings().getStringParam().toLowerCase())) {
                Habbo habbo = room.getHabbo(roomUnit);
                return !ownerOnly || (habbo != null && room.getOwnerId() == habbo.getHabboInfo().getId());
            }
        }

        return false;
    }

    @Override
    public String getWiredData() {
        return "";
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public boolean saveData() {
        return true;
    }

    @Override
    public boolean isTriggeredByRoomUnit() {
        return true;
    }
}
