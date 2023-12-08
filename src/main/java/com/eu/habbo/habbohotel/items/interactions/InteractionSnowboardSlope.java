package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.RoomItemManager;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionSnowboardSlope extends InteractionMultiHeight {
    public InteractionSnowboardSlope(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    public InteractionSnowboardSlope(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
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
    public void onPlace(Room room) {
        super.onPlace(room);
        THashSet<RoomItem> items = room.getRoomSpecialTypes().getItemsOfType(InteractionSnowboardSlope.class);

        Achievement snowboardBuild = Emulator.getGameEnvironment().getAchievementManager().getAchievement("snowBoardBuild");

        if (snowboardBuild == null) return;
        int progress;
        Habbo habbo = room.getRoomUnitManager().getRoomHabboById(room.getRoomInfo().getOwnerInfo().getId());

        if (habbo != null) {
            progress = habbo.getHabboStats().getAchievementProgress(snowboardBuild);


        } else {
            progress = AchievementManager.getAchievementProgressForHabbo(room.getRoomInfo().getOwnerInfo().getId(), snowboardBuild);
        }

        progress = Math.max(items.size() - progress, 0);

        if (progress > 0) {
            AchievementManager.progressAchievement(room.getRoomInfo().getOwnerInfo().getId(), snowboardBuild);
        }
    }

    @Override
    public void onPickUp(Room room) {
        for (Habbo habbo : room.getHabbosOnItem(this)) {
            if (habbo.getRoomUnit().getEffectId() == 97) {
                habbo.getRoomUnit().giveEffect(0, -1);
            }
        }
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        Rectangle newRect = RoomLayout.getRectangle(newLocation.getX(), newLocation.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());

        for (Habbo habbo : room.getHabbosOnItem(this)) {
            if (habbo.getRoomUnit().getEffectId() == 97 && !newRect.contains(habbo.getRoomUnit().getCurrentPosition().getX(), habbo.getRoomUnit().getCurrentPosition().getY())) {
                habbo.getRoomUnit().giveEffect(0, -1);
            }
        }
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