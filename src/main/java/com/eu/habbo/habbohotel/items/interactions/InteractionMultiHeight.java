package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class InteractionMultiHeight extends RoomItem {
    public InteractionMultiHeight(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    public InteractionMultiHeight(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
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
        return this.getBaseItem().allowWalk();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (client != null && !room.getRoomRightsManager().hasRights(client.getHabbo()) && !(objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE)) {
            return;
        }

        if (objects.length == 0) {
            return;
        }

        if (objects[0] instanceof Integer && room != null) {
            RoomItem topItem = room.getRoomItemManager().getTopItemAt(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());

            if (topItem != null && !topItem.equals(this)) { // multiheight items cannot change height even if there is a stackable item on top - no items allowed on top
                return;
            }

            this.needsUpdate(true);

            if (this.getExtraData().length() == 0) {
                this.setExtraData("0");
            }

            if (this.getBaseItem().getMultiHeights().length > 0) {
                this.setExtraData(String.valueOf((Integer.parseInt(this.getExtraData()) + 1) % (this.getBaseItem().getMultiHeights().length)));
                this.needsUpdate(true);
                room.updateTiles(room.getLayout().getTilesAt(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()));
                room.updateItemState(this);
            }
        }
    }

    public void updateUnitsOnItem(Room room) {
        THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());

        for(RoomTile tile : occupiedTiles) {
            Collection<RoomUnit> unitsOnItem = room.getRoomUnitManager().getRoomUnitsAt(room.getLayout().getTile(tile.getX(), tile.getY()));

            for (RoomUnit unit : unitsOnItem) {
                if (unit.hasStatus(RoomUnitStatus.MOVE)) {
                    if (unit.getTargetPosition() != tile) {
                        continue;
                    }
                }

                if (this.getBaseItem().allowSit() || unit.hasStatus(RoomUnitStatus.SIT)) {
                    unit.setSitUpdate(true);
                    unit.setStatusUpdateNeeded(true);
                } else {
                    unit.setCurrentZ(unit.getCurrentPosition().getStackHeight());
                }
            }
        }

    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {
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
}
