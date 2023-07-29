package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RoomUnitTeleportWalkToAction implements Runnable {

    private final Habbo habbo;
    private final RoomItem roomItem;
    private final Room room;


    @Override
    public void run() {
        if (this.habbo.getRoomUnit().getRoom() != this.room || this.roomItem.getRoomId() != this.room.getRoomInfo().getId()) {
            return;
        }
        RoomTile tile = RoomItem.getSquareInFront(this.room.getLayout(), this.roomItem);

        if (!this.habbo.getRoomUnit().getTargetPosition().equals(tile)) {
            return;
        }

        if (this.habbo.getRoomUnit().getCurrentPosition().equals(tile)) {
            try {
                this.roomItem.onClick(this.habbo.getClient(), this.room, new Object[]{0});
            } catch (Exception e) {
                log.error("Caught exception", e);
            }
        } else if (tile.isWalkable()) {
            this.habbo.getRoomUnit().setGoalLocation(tile);
            Emulator.getThreading().run(this, (long) this.habbo.getRoomUnit().getPath().size() + 2 * 510);
        }

    }
}