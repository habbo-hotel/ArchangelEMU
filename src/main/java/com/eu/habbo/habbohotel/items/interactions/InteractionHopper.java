package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.hopper.HopperActionOne;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHopper extends RoomItem {
    public InteractionHopper(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionHopper(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
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
        return false;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (room != null) {
            RoomTile loc = RoomItem.getSquareInFront(room.getLayout(), this);
            if (loc != null) {
                if (this.canUseTeleport(client, loc, room)) {
                    client.getHabbo().getRoomUnit().setTeleporting(true);
                    this.setExtraData("1");
                    room.updateItemState(this);

                    Emulator.getThreading().run(new HopperActionOne(this, room, client), 500);
                } else {
                    client.getHabbo().getRoomUnit().walkTo(loc);
                }
            }
        }
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");
    }

    @Override
    public void run() {
        if (!this.getExtraData().equals("0")) {
            this.setExtraData("0");

            Room room = this.getRoom();
            if (room != null) {
                room.updateItemState(this);
            }
        }
        super.run();
    }

    protected boolean canUseTeleport(GameClient client, RoomTile front, Room room) {
        if (client.getHabbo().getRoomUnit().getCurrentPosition().getX() != front.getX())
            return false;

        if (client.getHabbo().getRoomUnit().getCurrentPosition().getY() != front.getY())
            return false;

        if (client.getHabbo().getRoomUnit().isTeleporting())
            return false;

        RoomTile tile = room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());

        if(tile == null) {
            return false;
        }

        if (room.getRoomUnitManager().hasHabbosAt(tile))
            return false;

        return this.getExtraData().equals("0");
    }
}
