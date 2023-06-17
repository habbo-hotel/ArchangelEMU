package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.WiredTriggerReset;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerRepeater extends InteractionWiredTrigger implements ICycleable, WiredTriggerReset {
    public final int PARAM_REPEAT_TIME = 0;
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
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().size() == 0) {
            this.getWiredSettings().getIntegerParams().add(1);
        }
    }

    @Override
    public void cycle(Room room) {
        this.counter += 500;
        if (this.counter >= this.getWiredSettings().getIntegerParams().get(PARAM_REPEAT_TIME) * 500) {
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

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.PERIODICALLY;
    }
}
