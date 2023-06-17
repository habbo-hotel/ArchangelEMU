package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.WiredTriggerReset;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.threading.runnables.WiredExecuteTask;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerAtTimeLong extends InteractionWiredTrigger implements WiredTriggerReset {
    public final int PARAM_EXECUTE_TIME = 0;
    public int taskId;

    public WiredTriggerAtTimeLong(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerAtTimeLong(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.taskId = 1;
        Emulator.getThreading().run(new WiredExecuteTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId())), this.getWiredSettings().getIntegerParams().get(PARAM_EXECUTE_TIME));
        return true;
    }

    @Override
    public void resetTimer() {
        this.taskId++;
        Emulator.getThreading().run(new WiredExecuteTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId())), this.getWiredSettings().getIntegerParams().get(PARAM_EXECUTE_TIME));
    }

    @Override
    public WiredTriggerType getType() {
        return WiredTriggerType.AT_GIVEN_TIME;
    }
}
