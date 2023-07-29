package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloseGate implements Runnable {
    private final RoomItem gate;
    private final Room room;

    @Override
    public void run() {
        if (this.gate.getRoomId() == this.room.getRoomInfo().getId()) {
            if (this.room.isLoaded()) {
                RoomTile tile = this.room.getLayout().getTile(this.gate.getCurrentPosition().getX(), this.gate.getCurrentPosition().getY());

                if(tile == null) {
                    return;
                }

                if (!this.room.getRoomUnitManager().hasHabbosAt(tile)) {
                    this.gate.setExtraData("0");
                    this.room.updateItem(this.gate);
                    this.gate.needsUpdate(true);
                }
            }
        }
    }
}
