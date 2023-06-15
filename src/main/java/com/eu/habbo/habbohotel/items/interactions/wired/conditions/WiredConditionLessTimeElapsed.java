package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
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

public class WiredConditionLessTimeElapsed extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.TIME_LESS_THAN;

    private int cycles;

    public WiredConditionLessTimeElapsed(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionLessTimeElapsed(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return (Emulator.getIntUnixTimestamp() - room.getLastTimerReset()) / 0.5 < this.cycles;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.cycles
        ));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        try {
            if (wiredData.startsWith("{")) {
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                this.cycles = data.cycles;
            } else {
                if (!wiredData.equals(""))
                    this.cycles = Integer.parseInt(wiredData);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public boolean saveData() {
        if(this.getWiredSettings().getIntegerParams().length < 1) return false;
        this.cycles = this.getWiredSettings().getIntegerParams()[0];
        return true;
    }

    static class JsonData {
        int cycles;

        public JsonData(int cycles) {
            this.cycles = cycles;
        }
    }
}
