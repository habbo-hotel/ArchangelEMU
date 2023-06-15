package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionHabboCount extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.USER_COUNT;
    private int lowerLimit = 0;
    private int upperLimit = 50;

    public WiredConditionHabboCount(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionHabboCount(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int count = room.getUserCount();

        return count >= this.lowerLimit && count <= this.upperLimit;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.lowerLimit,
                this.upperLimit
        ));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.lowerLimit = data.lowerLimit;
            this.upperLimit = data.upperLimit;
        } else {
            String[] data = wiredData.split(":");

            this.lowerLimit = Integer.parseInt(data[0]);
            this.upperLimit = Integer.parseInt(data[1]);
        }
    }
    
    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public boolean saveData() {
        if(this.getWiredSettings().getIntegerParams().length < 2) return false;
        this.lowerLimit = this.getWiredSettings().getIntegerParams()[0];
        this.upperLimit = this.getWiredSettings().getIntegerParams()[1];

        return true;
    }

    static class JsonData {
        int lowerLimit;
        int upperLimit;

        public JsonData(int lowerLimit, int upperLimit) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }
    }
}
