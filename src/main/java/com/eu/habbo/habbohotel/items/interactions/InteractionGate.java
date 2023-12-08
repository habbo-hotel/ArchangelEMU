package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionGate extends RoomItem {
    public InteractionGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionGate(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    public boolean isWalkable() {
        return this.getExtraData().equals("1");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        boolean executedByWired = (objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE);

        if (client != null && !room.getRoomRightsManager().hasRights(client.getHabbo()) && !executedByWired) return;

        // If a Habbo is standing on a tile occupied by the gate, the gate shouldn't open/close
        for (RoomTile tile : room.getLayout().getTilesAt(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()))
            if (room.getRoomUnitManager().hasHabbosAt(tile))
                return;

        // Gate closed = 0, open = 1
        if (this.getExtraData().length() == 0)
            this.setExtraData("0");

        this.setExtraData((Integer.parseInt(this.getExtraData()) + 1) % 2 + "");
        room.updateTile(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
        this.setSqlUpdateNeeded(true);
        room.updateItemState(this);

        super.onClick(client, room, new Object[]{"TOGGLE_OVERRIDE"});
    }

    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
