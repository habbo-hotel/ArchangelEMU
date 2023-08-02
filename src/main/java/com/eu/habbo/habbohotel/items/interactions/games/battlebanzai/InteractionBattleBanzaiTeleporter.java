package com.eu.habbo.habbohotel.items.interactions.games.battlebanzai;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.BanzaiRandomTeleport;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBattleBanzaiTeleporter extends RoomItem {
    public InteractionBattleBanzaiTeleporter(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionBattleBanzaiTeleporter(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.setExtraData("0");
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {

    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if(objects.length < 3) {
            RoomItem target = room.getRoomSpecialTypes().getRandomTeleporter(null, this);
            if (target == null) return;

            this.setExtraData("1");
            room.updateItemState(this);
            roomUnit.removeStatus(RoomUnitStatus.MOVE);
            roomUnit.walkTo(roomUnit.getCurrentPosition());
            roomUnit.setCanWalk(false);
            Emulator.getThreading().run(new BanzaiRandomTeleport(this, target, roomUnit, room), 500);
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
    }
}
