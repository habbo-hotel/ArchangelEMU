package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomItemManager;
import com.eu.habbo.habbohotel.rooms.RoomMoodlightData;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.TIntObjectMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMoodLight extends RoomItem {
    public InteractionMoodLight(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionMoodLight(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
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
    public void onPlace(Room room) {
        if (room != null) {
            for (RoomMoodlightData data : ((TIntObjectMap<RoomMoodlightData>) room.getRoomInfo().getMoodLightData()).valueCollection()) {
                if (data.isEnabled()) {
                    this.setExtraData(data.toString());
                    this.setSqlUpdateNeeded(true);
                    room.updateItem(this);
                    Emulator.getThreading().run(this);
                }
            }
        }

        super.onPlace(room);
    }

    @Override
    public boolean isUsable() {
        return true;
    }
    @Override
    public void removeThisItem(RoomItemManager roomItemManager) {
        synchronized (roomItemManager.getUndefinedSpecials()) {
            roomItemManager.getUndefinedSpecials().remove(getId());
        }
    }

    @Override
    public void addThisItem(RoomItemManager roomItemManager) {
        synchronized (roomItemManager.getUndefinedSpecials()) {
            roomItemManager.getUndefinedSpecials().put(getId(), this);
        }
    }
}
