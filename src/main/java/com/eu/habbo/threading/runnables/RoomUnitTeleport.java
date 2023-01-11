package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.HabboItem;
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

        RoomTile lastLocation = this.roomUnit.getCurrentLocation();
        RoomTile newLocation = this.room.getLayout().getTile((short) this.x, (short) this.y);

        HabboItem topItem = this.room.getTopItemAt(this.roomUnit.getCurrentLocation().getX(), this.roomUnit.getCurrentLocation().getY());
        if (topItem != null) {
            try {
                topItem.onWalkOff(this.roomUnit, this.room, new Object[]{this});
            } catch (Exception e) {
                log.error("Caught exception", e);
            }
        }
        this.roomUnit.setPath(new LinkedList<>());
        this.roomUnit.setCurrentLocation(newLocation);
        this.roomUnit.setPreviousLocation(newLocation);
        this.roomUnit.setZ(this.z);
        this.roomUnit.setPreviousLocationZ(this.z);
        this.roomUnit.removeStatus(RoomUnitStatus.MOVE);
        //ServerMessage teleportMessage = new RoomUnitOnRollerComposer(this.roomUnit, newLocation, this.room).compose();
        this.roomUnit.setLocation(newLocation);
        //this.room.sendComposer(teleportMessage);
        this.roomUnit.statusUpdate(true);
        roomUnit.setWiredTeleporting(false);

        this.room.updateHabbosAt(newLocation.getX(), newLocation.getY());
        this.room.updateBotsAt(newLocation.getX(), newLocation.getY());

        topItem = room.getTopItemAt(x, y);
        if (topItem != null && roomUnit.getCurrentLocation().equals(room.getLayout().getTile((short) x, (short) y))) {
            try {
                topItem.onWalkOn(roomUnit, room, new Object[]{ lastLocation, newLocation, this });
            } catch (Exception ignored) {
            }
        }
    }
}
