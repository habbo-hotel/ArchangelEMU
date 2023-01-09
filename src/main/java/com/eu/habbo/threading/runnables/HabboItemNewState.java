package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboItemNewState implements Runnable {
    private final HabboItem item;
    private final Room room;
    private final String state;

    @Override
    public void run() {
        this.item.setExtradata(this.state);

        if (this.item.getRoomId() == this.room.getId()) {
            this.room.updateItemState(this.item);
        }
    }
}
