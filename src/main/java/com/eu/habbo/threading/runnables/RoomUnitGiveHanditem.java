package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomUnitGiveHanditem implements Runnable {
    private final RoomAvatar roomAvatar;
    private final Room room;
    private final int itemId;

    @Override
    public void run() {
        if (this.room != null && this.roomAvatar.isInRoom()) {
            this.roomAvatar.setHandItem(this.itemId);
            this.room.sendComposer(new CarryObjectMessageComposer(this.roomAvatar).compose());
        }
    }
}
