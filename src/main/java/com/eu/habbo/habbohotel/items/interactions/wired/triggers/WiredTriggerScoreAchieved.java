package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerScoreAchieved extends InteractionWiredTrigger {
    public final int PARAM_SCORE = 0;

    public WiredTriggerScoreAchieved(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerScoreAchieved(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (stuff.length >= 2) {
            int points = (Integer) stuff[0];
            int amountAdded = (Integer) stuff[1];

            return points - amountAdded < this.getWiredSettings().getIntegerParams().get(PARAM_SCORE) && points >= this.getWiredSettings().getIntegerParams().get(PARAM_SCORE);
        }

        return false;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().size() == 0) {
            this.getWiredSettings().getIntegerParams().add(1);
        }
    }

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.SCORE_ACHIEVED;
    }
}
