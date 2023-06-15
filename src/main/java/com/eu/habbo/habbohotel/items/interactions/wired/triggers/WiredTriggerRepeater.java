package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredTriggerReset;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredTriggerRepeater extends InteractionWiredTrigger implements ICycleable, WiredTriggerReset {
    public static final WiredTriggerType type = WiredTriggerType.PERIODICALLY;
    public static final int DEFAULT_DELAY = 10 * 500;

    protected int repeatTime = DEFAULT_DELAY;
    protected int counter = 0;

    public WiredTriggerRepeater(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerRepeater(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
            this.repeatTime
        ));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.repeatTime = data.repeatTime;
        } else {
            if (wiredData.length() >= 1) {
                this.repeatTime = (Integer.parseInt(wiredData));
            }
        }

        if (this.repeatTime < 500) {
            this.repeatTime = 20 * 500;
        }
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public boolean saveData() {
        if(this.getWiredSettings().getIntegerParams().length < 1) return false;
        this.repeatTime = this.getWiredSettings().getIntegerParams()[0] * 500;
        this.counter = 0;

        if (this.repeatTime < 500) {
            this.repeatTime = 500;
        }

        return true;
    }

    @Override
    public void cycle(Room room) {
        this.counter += 500;
        if (this.counter >= this.repeatTime) {
            this.counter = 0;
            if (this.getRoomId() != 0) {
                if (room.isLoaded()) {
                    WiredHandler.handle(this, null, room, new Object[]{this});
                }
            }
        }
    }

    @Override
    public void resetTimer() {
        this.counter = 0;
        if (this.getRoomId() != 0) {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            if (room != null && room.isLoaded()) {
                WiredHandler.handle(this, null, room, new Object[]{this});
            }
        }
    }

    static class JsonData {
        int repeatTime;

        public JsonData(int repeatTime) {
            this.repeatTime = repeatTime;
        }
    }
}
