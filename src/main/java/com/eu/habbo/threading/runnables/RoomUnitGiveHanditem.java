package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomUnitGiveHanditem implements Runnable {
    private final RoomUnit roomUnit;
    private final Room room;
    private final int itemId;

    @Override
    public void run() {
        if (this.room != null && this.roomUnit.isInRoom()) {
            this.roomUnit.setHandItem(this.itemId);
            this.room.sendComposer(new CarryObjectMessageComposer(this.roomUnit).compose());
        }
    }
}
