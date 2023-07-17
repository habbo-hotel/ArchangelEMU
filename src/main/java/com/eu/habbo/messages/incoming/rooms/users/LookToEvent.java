package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class LookToEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getRoomUnit().getRoom();
        if (room == null)
            return;

        Habbo habbo = this.client.getHabbo();

        if (habbo.getRoomUnit().getCacheable().get("control") != null) {
            habbo = (Habbo) this.client.getHabbo().getRoomUnit().getCacheable().get("control");

            if (habbo.getRoomUnit().getRoom() != this.client.getHabbo().getRoomUnit().getRoom()) {
                habbo.getRoomUnit().getCacheable().remove("controller");
                this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                habbo = this.client.getHabbo();
            }
        }

        RoomUnit roomUnit = habbo.getRoomUnit();

        if (!roomUnit.isCanWalk())
            return;

        if (roomUnit.isWalking() || roomUnit.hasStatus(RoomUnitStatus.MOVE))
            return;

        if (roomUnit.isCmdLayEnabled() || roomUnit.hasStatus(RoomUnitStatus.LAY))
            return;

        if (roomUnit.isIdle())
            return;

        int x = this.packet.readInt();
        int y = this.packet.readInt();

        if (x == roomUnit.getCurrentPosition().getX()) {
            if (y == roomUnit.getCurrentPosition().getY()) return;
        }

        RoomTile tile = habbo.getRoomUnit().getRoom().getLayout().getTile((short) x, (short) y);

        if (tile != null) {
            roomUnit.lookAtPoint(tile);
            roomUnit.setStatusUpdateNeeded(true);
            //room.sendComposer(new RoomUserStatusComposer(roomUnit).compose());
        }
    }
}
