package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredPeriodical;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.WiredTriggerReset;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerRepeaterLong extends InteractionWiredTrigger implements IWiredPeriodical, WiredTriggerReset {
    public final int PARAM_REPEAT_TIME = 0;
    protected int counter = 0;
    @Setter
    private int interval;
    public WiredTriggerRepeaterLong(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerRepeaterLong(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return true;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().size() == 0) {
            this.getWiredSettings().getIntegerParams().add(1);
        }
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        if(room.getTriggersOnRoom().containsKey(oldLocation)) {
            if(room.getTriggersOnRoom().get(oldLocation).getId() == this.getId()) {
                room.getTriggersOnRoom().remove(oldLocation);
            }
        }

        super.onMove(room, oldLocation, newLocation);
    }

    @Override
    public void onPickUp(Room room) {
        RoomTile oldLocation = room.getLayout().getTile(this.getX(), this.getY());

        if(room.getTriggersOnRoom().containsKey(oldLocation)) {
            if(room.getTriggersOnRoom().get(oldLocation).getId() == this.getId()) {
                room.getTriggersOnRoom().remove(oldLocation);
            }
        }

        super.onPickUp(room);
    }

    public int getInterval() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_REPEAT_TIME) * 5000;
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
        return WiredTriggerType.PERIODICALLY_LONG;
    }
}
