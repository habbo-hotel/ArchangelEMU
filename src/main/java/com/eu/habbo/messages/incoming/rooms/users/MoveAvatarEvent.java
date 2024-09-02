package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
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
//        int x = this.packet.readInt();
//        int y = this.packet.readInt();
//
//        Habbo habbo = this.client.getHabbo();
//
//        if(habbo == null) {
//            return;
//        }
//
//        if (habbo.getHabboRoleplayStats().isDead()) {
//            habbo.whisper(Emulator.getTexts().getValue("roleplay.dead.you_cant_walk"));
//            return;
//        }
//
//        RoomHabbo roomHabbo = this.client.getHabbo().getRoomUnit();
//
//        if(roomHabbo == null) {
//            return;
//        }
//
//        Room room = habbo.getRoomUnit().getRoom();
//
//        if (room == null || room.getLayout() == null) {
//            return;
//        }
//
//        if (roomHabbo.getCurrentPosition().equals(x,y) && roomHabbo.isAtGoal()) {
//            return;
//        }
//
//        if (roomHabbo.getCacheable().get("control") != null) {
//            habbo = (Habbo) roomHabbo.getCacheable().get("control");
//
//            if (habbo.getRoomUnit().getRoom() != room) {
//                habbo.getRoomUnit().getCacheable().remove("controller");
//                this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
//                habbo = this.client.getHabbo();
//            }
//        }
//
//        roomHabbo = habbo.getRoomUnit();
//
//        if (roomHabbo != null && roomHabbo.isInRoom() && roomHabbo.isCanWalk()) {
//            RoomTile tile = room.getLayout().getTile((short) x, (short) y);
//
//            if (tile == null) {
//                return;
//            }
//
//            //STANKMAN EASTER EGG
//            roomHabbo.setTemporalFastWalkEnabled(roomHabbo.getTargetPosition() != null && roomHabbo.getTargetPosition().equals(tile));
//
//            roomHabbo.walkTo(tile);
//        }
    }
}
