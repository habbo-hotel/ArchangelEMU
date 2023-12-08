package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPuzzleBox extends RoomItem {
    public InteractionPuzzleBox(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPuzzleBox(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        RoomTile boxLocation = room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());
        RoomRotation rotation = null;

        if (this.getCurrentPosition().getX() == client.getHabbo().getRoomUnit().getCurrentPosition().getX()) {
            if (this.getCurrentPosition().getY() == client.getHabbo().getRoomUnit().getCurrentPosition().getY() + 1) {
                rotation = RoomRotation.SOUTH;
            } else {
                if (this.getCurrentPosition().getY() == client.getHabbo().getRoomUnit().getCurrentPosition().getY() - 1) {
                    rotation = RoomRotation.NORTH;
                }
            }
        } else {
            if (this.getCurrentPosition().getY() == client.getHabbo().getRoomUnit().getCurrentPosition().getY()) {
                if (this.getCurrentPosition().getX() == client.getHabbo().getRoomUnit().getCurrentPosition().getX() + 1) {
                    rotation = RoomRotation.EAST;
                } else if (this.getCurrentPosition().getX() == client.getHabbo().getRoomUnit().getCurrentPosition().getX() - 1) {
                    rotation = RoomRotation.WEST;
                }
            }
        }

        if (rotation == null) {
            RoomTile nearestTile = client.getHabbo().getRoomUnit().getClosestAdjacentTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), false);

            if (nearestTile != null) client.getHabbo().getRoomUnit().walkTo(nearestTile);
            return;
        }

        super.onClick(client, room, new Object[]{"TOGGLE_OVERRIDE"});

        RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), rotation.getValue());

        if (tile == null || tile.getState() == RoomTileState.INVALID || room.getRoomUnitManager().hasHabbosAt(tile)) {
            return;
        }

        if (!boxLocation.equals(room.getLayout().getTileInFront(client.getHabbo().getRoomUnit().getCurrentPosition(), rotation.getValue())))
            return;

        RoomItem item = room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY());

        if (item != null && !room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY()).getBaseItem().allowStack())
            return;

        this.setCurrentZ(room.getStackHeight(tile.getX(), tile.getY(), false));
        this.setSqlUpdateNeeded(true);

        room.updateItem(this);

        room.scheduledComposers.add(new FloorItemOnRollerComposer(this, null, tile, 0, room).compose());
        room.scheduledTasks.add(() -> {
            client.getHabbo().getRoomUnit().walkTo(boxLocation);

            room.scheduledTasks.add(() -> client.getHabbo().getRoomUnit().walkTo(boxLocation));
        });
        this.setSqlUpdateNeeded(true);
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
}
