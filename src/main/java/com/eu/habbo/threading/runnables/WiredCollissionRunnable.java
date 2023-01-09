package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WiredCollissionRunnable implements Runnable {
    public final RoomUnit roomUnit;
    public final Room room;
    public final Object[] objects;



    @Override
    public void run() {
        WiredHandler.handle(WiredTriggerType.COLLISION, roomUnit, room, objects);
    }
}
