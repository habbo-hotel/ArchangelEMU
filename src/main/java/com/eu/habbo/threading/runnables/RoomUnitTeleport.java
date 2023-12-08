package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j
@AllArgsConstructor
public class RoomUnitTeleport implements Runnable {

    
    private final RoomUnit roomUnit;
    private final Room room;
    private final int x;
    private final int y;
    private final double z;

    private final int newEffect;

    @Override
    public void run() {
        if (roomUnit == null || roomUnit.getRoom() == null || room.getLayout() == null)
            return;

        if (roomUnit.isLeavingTeleporter()) {
            roomUnit.setWiredTeleporting(false);
            return;
        }

        RoomTile lastLocation = this.roomUnit.getCurrentPosition();
        RoomTile newLocation = this.room.getLayout().getTile((short) this.x, (short) this.y);

        RoomItem topItem = this.room.getRoomItemManager().getTopItemAt(this.roomUnit.getCurrentPosition().getX(), this.roomUnit.getCurrentPosition().getY());
        if (topItem != null) {
            try {
                topItem.onWalkOff(this.roomUnit, this.room, new Object[]{this});
            } catch (Exception e) {
                log.error("Caught exception", e);
            }
        }
        this.roomUnit.setPath(new LinkedList<>());
        this.roomUnit.setCurrentPosition(newLocation);
        this.roomUnit.setCurrentZ(this.z);
        this.roomUnit.removeStatus(RoomUnitStatus.MOVE);
        //ServerMessage teleportMessage = new RoomUnitOnRollerComposer(this.roomUnit, newLocation, this.room).compose();
        this.roomUnit.setLocation(newLocation);
        //this.room.sendComposer(teleportMessage);
        this.roomUnit.setStatusUpdateNeeded(true);
        roomUnit.setWiredTeleporting(false);

        this.room.getRoomUnitManager().updateHabbosAt(newLocation);
        this.room.getRoomUnitManager().getRoomBotManager().updateBotsAt(newLocation);

        topItem = room.getRoomItemManager().getTopItemAt(x, y);
        if (topItem != null && roomUnit.getCurrentPosition().equals(room.getLayout().getTile((short) x, (short) y))) {
            try {
                topItem.onWalkOn(roomUnit, room, new Object[]{ lastLocation, newLocation, this });
            } catch (Exception ignored) {
            }
        }
    }
}
