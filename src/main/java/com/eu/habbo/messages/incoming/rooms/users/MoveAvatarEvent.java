package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveAvatarEvent extends MessageHandler {
    @Override
    public int getRatelimit() {
        return 500;
    }

    @Override
    public void handle() throws Exception {
        int x = this.packet.readInt();
        int y = this.packet.readInt();

        Habbo habbo = this.client.getHabbo();

        if(habbo == null) {
            return;
        }

        RoomHabbo roomHabbo = this.client.getHabbo().getRoomUnit();

        if(roomHabbo == null) {
            return;
        }

        Room room = habbo.getRoomUnit().getRoom();

        if (room == null || room.getLayout() == null) {
            return;
        }

        if (roomHabbo.getCurrentPosition().equals(x,y)) {
            return;
        }

        if (roomHabbo.getCacheable().get("control") != null) {
            habbo = (Habbo) roomHabbo.getCacheable().get("control");

            if (habbo.getRoomUnit().getRoom() != room) {
                habbo.getRoomUnit().getCacheable().remove("controller");
                this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                habbo = this.client.getHabbo();
            }
        }

        roomHabbo = habbo.getRoomUnit();

        if (roomHabbo != null && roomHabbo.isInRoom() && roomHabbo.isCanWalk()) {
            RoomTile tile = room.getLayout().getTile((short) x, (short) y);

            if (tile == null) {
                return;
            }

            log.info("CLICKED ON TILE [x]: {} [y]: {}", tile.getX(), tile.getY());
            roomHabbo.walkTo(tile);
        }
    }
}
