package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionGymEquipment extends InteractionEffectTile implements ICycleable {
    private int startTime = 0;
    private int roomUnitId = -1;

    public InteractionGymEquipment(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionGymEquipment(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return this.roomUnitId == -1 && super.canWalkOn(roomUnit, room, objects) && (roomUnit.getRoomUnitType().equals(RoomUnitType.HABBO) || roomUnit.getRoomUnitType().equals(RoomUnitType.BOT));
    }

    @Override
    public boolean isWalkable() {
        return this.roomUnitId == -1;
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if (this.forceRotation()) {
            roomUnit.setRotation(RoomRotation.fromValue(this.getRotation()));
            roomUnit.setCanRotate(false);
        }
        this.roomUnitId = roomUnit.getVirtualId();

        if (roomUnit.getRoomUnitType() == RoomUnitType.HABBO) {
            Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

            if (habbo != null) {
                this.startTime = Emulator.getIntUnixTimestamp();
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);

        if (room == null) return;

        this.reset(room);

        if (roomUnit instanceof RoomAvatar roomAvatar) {
            Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomAvatar);
            RoomItem topItem = room.getRoomItemManager().getTopItemAt(roomAvatar.getCurrentPosition().getX(), roomAvatar.getCurrentPosition().getY());
            int nextEffectM = 0;
            int nextEffectF = 0;
            int nextEffectDuration = -1;

            if (topItem != null) {
                nextEffectM = topItem.getBaseItem().getEffectM();
                nextEffectF = topItem.getBaseItem().getEffectF();
            } else if (roomAvatar.getPreviousEffectId() > 0) {
                nextEffectF = roomAvatar.getPreviousEffectId();
                nextEffectM = roomAvatar.getPreviousEffectId();
                nextEffectDuration = roomAvatar.getPreviousEffectEndTimestamp();
            }

            if (this.forceRotation()) {
                roomAvatar.setCanRotate(true);
            }

            if (habbo.getHabboInfo().getGender().equals(HabboGender.M)) {
                roomAvatar.giveEffect(nextEffectM, nextEffectDuration, true);
                return;
            }

            if (habbo.getHabboInfo().getGender().equals(HabboGender.F)) {
                roomAvatar.giveEffect(nextEffectF, nextEffectDuration, true);
            }
        }
    }

    public String achievementName() {
        return Emulator.getConfig().getValue("hotel.furni.gym.achievement." + this.getBaseItem().getName(), "");
    }

    public boolean forceRotation() {
        return Emulator.getConfig().getBoolean("hotel.furni.gym.forcerot." + this.getBaseItem().getName(), true);
    }

    @Override
    public void cycle(Room room) {
        if (this.roomUnitId != -1) {
            Habbo habbo = room.getRoomUnitManager().getHabboByVirtualId(this.roomUnitId);

            if (habbo != null) {
                int timestamp = Emulator.getIntUnixTimestamp();
                if (timestamp - this.startTime >= 120) {
                    String achievement = this.achievementName();

                    if (!achievement.isEmpty()) {
                        AchievementManager.progressAchievement(habbo.getHabboInfo().getId(), Emulator.getGameEnvironment().getAchievementManager().getAchievement(achievement));
                    }

                    this.startTime = timestamp;
                }
            }
        }
    }

    @Override
    public void setRotation(int rotation) {
        super.setRotation(rotation);

        if (this.forceRotation() && this.roomUnitId != -1) {
            Room room = this.getRoom();
            if (room != null) {
                RoomUnit roomUnit = this.getCurrentRoomUnit(room);

                if (roomUnit != null) {
                    roomUnit.setRotation(RoomRotation.fromValue(rotation));
                    room.updateRoomUnit(roomUnit);
                }
            }
        }
    }

    @Override
    public void onPickUp(Room room) {
        super.onPickUp(room);

        if (this.roomUnitId != -1) {
            this.setEffect(room, 0);
        }

        this.reset(room);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        if (!oldLocation.equals(newLocation)) {
            this.setEffect(room, 0);
            this.reset(room);
        }
    }

    private void setEffect(Room room, int effectId) {
        if (this.roomUnitId == -1) return;

        RoomUnit roomUnit = this.getCurrentRoomUnit(room);

        if(roomUnit instanceof RoomAvatar roomAvatar) {
            roomAvatar.giveEffect(effectId, -1);
        }
    }

    private void reset(Room room) {
        this.roomUnitId = -1;
        this.startTime = 0;
        this.setExtraData("0");
        room.updateItem(this);
    }

    private RoomUnit getCurrentRoomUnit(Room room) {
        return room.getRoomUnitManager().getCurrentRoomUnits().get(this.roomUnitId);
    }
}