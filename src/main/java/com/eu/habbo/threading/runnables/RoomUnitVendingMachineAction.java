package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RoomUnitVendingMachineAction implements Runnable {

    private final Habbo habbo;
    private final HabboItem habboItem;
    private final Room room;

    @Override
    public void run() {
        if (this.habbo.getHabboInfo().getCurrentRoom() != this.room || this.habboItem.getRoomId() != this.room.getId()) {
            return;
        }

        RoomTile tile = HabboItem.getSquareInFront(this.room.getLayout(), this.habboItem);
        if (tile == null) {
            return;
        }

        if (this.habbo.getRoomUnit().getGoalLocation().equals(tile)) {
            if (this.habbo.getRoomUnit().getCurrentLocation().equals(tile)) {
                try {
                    this.habboItem.onClick(this.habbo.getClient(), this.room, new Object[]{0});
                } catch (Exception e) {
                    log.error("Caught exception", e);
                }
            } else {
                if (this.room.getLayout().getTile(tile.getX(), tile.getY()).isWalkable()) {
                    this.habbo.getRoomUnit().setGoalLocation(tile);
                    Emulator.getThreading().run(this, (long) this.habbo.getRoomUnit().getPath().size() + 2 * 510);
                }
            }
        }
    }
}

