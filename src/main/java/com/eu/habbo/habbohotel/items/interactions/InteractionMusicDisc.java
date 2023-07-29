package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class InteractionMusicDisc extends RoomItem {

    private int songId;

    public InteractionMusicDisc(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);

        String[] stuff = this.getExtraData().split("\n");

        if (stuff.length >= 7 && !stuff[6].isEmpty()) {
            try {
                this.songId = Integer.parseInt(stuff[6]);
            } catch (Exception e) {
                log.error("Warning: Item " + this.getId() + " has an invalid song id set for its music disk!");
            }
        }
    }

    public InteractionMusicDisc(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);

        String[] stuff = this.getExtraData().split("\n");

        if (stuff.length >= 7 && !stuff[6].isEmpty()) {
            try {
                this.songId = Integer.parseInt(stuff[6]);
            } catch (Exception e) {
                log.error("Warning: Item " + this.getId() + " has an invalid song id set for its music disk!");
            }
        }
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

    public int getSongId() {
        return this.songId;
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);

        room.getRoomTraxManager().sendUpdatedSongList();
    }

    @Override
    public void onPickUp(Room room) {
        super.onPickUp(room);

        room.getRoomTraxManager().sendUpdatedSongList();
    }
}
