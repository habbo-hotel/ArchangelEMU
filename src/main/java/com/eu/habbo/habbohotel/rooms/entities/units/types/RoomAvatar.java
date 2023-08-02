package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.DanceMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RoomAvatar extends RoomUnit {
    protected RideablePet ridingPet;
    protected boolean rideLocked;
    protected DanceType danceType;
    protected int handItem;
    protected long handItemTimestamp;
    protected int effectId;
    protected int effectEndTimestamp;
    protected int previousEffectId;
    protected int previousEffectEndTimestamp;

    public RoomAvatar() {
        super();

        this.ridingPet = null;
        this.danceType = DanceType.NONE;
        this.handItem = 0;
        this.handItemTimestamp = 0;
        this.effectId = 0;
        this.effectEndTimestamp = -1;
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;
    }

    @Override
    public void cycle() {
        this.handleSignStatus();
        this.processWalking();
    }

    @Override
    public boolean walkTo(RoomTile goalLocation) {
        if (this.hasStatus(RoomUnitStatus.LAY)) {
            if (this.room.getLayout().getTilesInFront(this.getCurrentPosition(), this.getBodyRotation().getValue(), 2).contains(goalLocation))
                return false;
        }

        if (this.room.canLayAt(goalLocation)) {
            RoomItem bed = this.room.getRoomItemManager().getTopItemAt(goalLocation.getX(), goalLocation.getY());

            if (bed != null && bed.getBaseItem().allowLay()) {
                this.room.getLayout().getTile(bed.getCurrentPosition().getX(), bed.getCurrentPosition().getY());
                RoomTile pillow = switch (bed.getRotation()) {
                    case 0, 4 -> this.room.getLayout().getTile(goalLocation.getX(), bed.getCurrentPosition().getY());
                    case 2, 8 -> this.room.getLayout().getTile(bed.getCurrentPosition().getX(), goalLocation.getY());
                    default -> this.room.getLayout().getTile(bed.getCurrentPosition().getX(), bed.getCurrentPosition().getY());
                };

                if (pillow != null && this.room.canLayAt(pillow)) {
                    goalLocation = pillow;
                }
            }
        }

        return super.walkTo(goalLocation);
    }

    public void dismountPet(boolean isRemoving) {
        if(!this.isRiding()) {
            return;
        }

        this.ridingPet.setRider(null);
        this.ridingPet.setTask(PetTasks.FREE);

        this.ridingPet = null;

        this.giveEffect(0, -1);
        this.setCurrentZ(this.ridingPet.getRoomUnit().getCurrentZ());
        this.stopWalking();

        this.ridingPet.getRoomUnit().stopWalking();

        this.instantUpdate();
        this.ridingPet.getRoomUnit().instantUpdate();

        List<RoomTile> availableTiles = isRemoving ? new ArrayList<>() : this.room.getLayout().getWalkableTilesAround(this.getCurrentPosition());

        RoomTile tile = availableTiles.isEmpty() ? this.getCurrentPosition() : availableTiles.get(0);
        this.walkTo(tile);
        this.setStatusUpdateNeeded(true);
    }

    public void setDance(DanceType danceType) {
        if (this.danceType != danceType) {
            boolean isDancing = !this.danceType.equals(DanceType.NONE);
            this.danceType = danceType;
            this.room.sendComposer(new DanceMessageComposer(this).compose());

            if (danceType.equals(DanceType.NONE) && isDancing) {
                WiredHandler.handle(WiredTriggerType.STOPS_DANCING, this, this.room, new Object[]{this});
            } else if (!danceType.equals(DanceType.NONE) && !isDancing) {
                WiredHandler.handle(WiredTriggerType.STARTS_DANCING, this, this.room, new Object[]{this});
            }
        }
    }

    public RoomAvatar setHandItem(int handItem) {
        this.handItem = handItem;
        this.handItemTimestamp = System.currentTimeMillis();
        return this;
    }

    public void giveEffect(int effectId, int duration) {
        this.giveEffect(effectId, duration, false);
    }

    public void giveEffect(int effectId, int duration, boolean forceEffect) {
        if (!this.isInRoom()) {
            return;
        }

        if(this.getEffectId() == effectId) {
            return;
        }

        if(this instanceof RoomHabbo) {
            Habbo habbo = this.room.getRoomUnitManager().getHabboByRoomUnit(this);
            if(habbo == null || (habbo.getHabboInfo().isInGame() && !forceEffect)) {
                return;
            }
        }

        if (duration == -1 || duration == Integer.MAX_VALUE) {
            duration = Integer.MAX_VALUE;
        } else {
            duration += Emulator.getIntUnixTimestamp();
        }

        if ((this.room.isAllowEffects() || forceEffect) && !this.isSwimming()) {
            this.effectId = effectId;
            this.effectEndTimestamp = duration;

            this.room.sendComposer(new AvatarEffectMessageComposer(this).compose());
        }
    }

    public void setPreviousEffectId(int effectId, int endTimestamp) {
        this.previousEffectId = effectId;
        this.previousEffectEndTimestamp = endTimestamp;
    }

    private void handleSignStatus() {
        if (this.hasStatus(RoomUnitStatus.SIGN)) {
            this.room.sendComposer(new UserUpdateComposer(this).compose());
            this.removeStatus(RoomUnitStatus.SIGN);
        }
    }

    public boolean isRiding() {
        return this.ridingPet != null;
    }

    @Override
    public void clear() {
        super.clear();

        this.ridingPet = null;
        this.danceType = DanceType.NONE;
        this.handItem = 0;
        this.handItemTimestamp = 0;
        this.effectId = 0;
        this.effectEndTimestamp = -1;
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;
    }
}
