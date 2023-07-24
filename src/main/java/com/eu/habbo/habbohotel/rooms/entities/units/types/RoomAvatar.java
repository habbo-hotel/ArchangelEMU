package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.interfaces.ConditionalGate;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.DanceMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.RoomUnitKick;
import com.eu.habbo.util.pathfinding.Rotation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RoomAvatar extends RoomUnit {
    private RideablePet rideablePet;
    private DanceType danceType;
    private int handItem;
    private long handItemTimestamp;
    private int effectId;
    private int effectEndTimestamp;
    private int previousEffectId;
    private int previousEffectEndTimestamp;

    public RoomAvatar() {
        super();

        this.rideablePet = null;
        this.danceType = DanceType.NONE;
        this.handItem = 0;
        this.handItemTimestamp = 0;
        this.effectId = 0;
        this.effectEndTimestamp = -1;
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;
    }

    @Override
    public boolean cycle(Room room) {
        try {
            if (this.hasStatus(RoomUnitStatus.SIGN)) {
                this.getRoom().sendComposer(new UserUpdateComposer(this).compose());
                this.removeStatus(RoomUnitStatus.SIGN);
            }

            Habbo habboT = room.getRoomUnitManager().getHabboByRoomUnit(this);

            if (!this.isWalking() && !this.isKicked() && this.removeStatus(RoomUnitStatus.MOVE) == null && habboT != null) {
                habboT.getHabboInfo().getRiding().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
                return true;
            }

            for (Map.Entry<RoomUnitStatus, String> set : this.getStatuses().entrySet()) {
                if (set.getKey().isRemoveWhenWalking()) {
                    this.removeStatus(set.getKey());
                }
            }

            if (this.getPath() == null || this.getPath().isEmpty()) {
                return true;
            }

            boolean canFastWalk = habboT == null || habboT.getHabboInfo().getRiding() == null;

            RoomTile next = this.getPath().poll();

            boolean overrideTile = next != null && this.canOverrideTile(next);

            if (this.getPath().isEmpty()) {
                this.setSitUpdate(true);

                if (next != null && room.getRoomUnitManager().areRoomUnitsAt(next) && !overrideTile) {
                    return false;
                }
            }

            Deque<RoomTile> peekPath = room.getLayout().findPath(this.getCurrentPosition(), this.getPath().peek(), this.getGoalLocation(), this);

            if (peekPath == null) {
                peekPath = new LinkedList<>();
            }

            if (peekPath.size() >= 3) {
                if (this.getPath().isEmpty()) {
                    this.setStatusUpdateNeeded(true);
                    return true;
                }

                this.getPath().pop();
                //peekPath.pop(); //Start
                peekPath.removeLast(); //End

                if (peekPath.peek() != next) {
                    next = peekPath.poll();
                    for (int i = 0; i < peekPath.size(); i++) {
                        this.getPath().addFirst(peekPath.removeLast());
                    }
                }
            }

            if (canFastWalk && this.isFastWalkEnabled() && this.getPath().size() > 1) {
                next = this.getPath().poll();
            }

            if (next == null) {
                this.setStatusUpdateNeeded(true);
                return true;
            }

            Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(this);

            this.removeStatus(RoomUnitStatus.DEAD);

            if (habbo != null) {
                if(this instanceof RoomHabbo roomHabbo) {
                    if (roomHabbo.isIdle()) {
                        UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                        Emulator.getPluginManager().fireEvent(event);

                        if (!event.isCancelled() && !event.isIdle()) {
                            roomHabbo.unIdle();
                        }
                    }
                }

                if (Emulator.getPluginManager().isRegistered(UserTakeStepEvent.class, false)) {
                    Event e = new UserTakeStepEvent(habbo, room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), next);
                    Emulator.getPluginManager().fireEvent(e);

                    if (e.isCancelled()) {
                        this.setStatusUpdateNeeded(true);
                        return true;
                    }

                }
            }

            RoomItem item = room.getRoomItemManager().getTopItemAt(next.getX(), next.getY());

            double height = next.getStackHeight() - this.getCurrentPosition().getStackHeight();

            if (!room.getLayout().tileWalkable(next) || (!RoomLayout.ALLOW_FALLING && height < -RoomLayout.MAXIMUM_STEP_HEIGHT) || (next.getState() == RoomTileState.OPEN && height > RoomLayout.MAXIMUM_STEP_HEIGHT)) {
                this.getPath().clear();
                this.findPath();

                if (this.getPath().isEmpty()) {
                    this.removeStatus(RoomUnitStatus.MOVE);
                    return false;
                }
                next = this.getPath().pop();

            }

            boolean canSitNextTile = room.canSitAt(next.getX(), next.getY());

            if (canSitNextTile) {
                RoomItem tallestChair = room.getRoomItemManager().getTallestChair(next);

                if (tallestChair != null)
                    item = tallestChair;
            }

            if (next.equals(this.getGoalLocation()) && next.getState() == RoomTileState.SIT && !overrideTile && (item == null || item.getZ() - this.getCurrentZ() > RoomLayout.MAXIMUM_STEP_HEIGHT)) {
                this.removeStatus(RoomUnitStatus.MOVE);
                return false;
            }

            double zHeight = 0.0D;

            if(habbo != null && habbo.getHabboInfo().getRiding() != null) {
                zHeight += 1.0D;
            }

            RoomItem roomItem = room.getRoomItemManager().getTopItemAt(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());
            if (roomItem != null && (roomItem != item || !RoomLayout.pointInSquare(roomItem.getX(), roomItem.getY(), roomItem.getX() + roomItem.getBaseItem().getWidth() - 1, roomItem.getY() + roomItem.getBaseItem().getLength() - 1, next.getX(), next.getY())))
                roomItem.onWalkOff(this, room, new Object[]{this.getCurrentPosition(), next});


            this.incrementTilesMoved();

            RoomRotation oldRotation = this.getBodyRotation();

            this.setRotation(RoomRotation.values()[Rotation.Calculate(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), next.getX(), next.getY())]);
            if (item != null) {
                if (item != roomItem || !RoomLayout.pointInSquare(item.getX(), item.getY(), item.getX() + item.getBaseItem().getWidth() - 1, item.getY() + item.getBaseItem().getLength() - 1, this.getCurrentPosition().getX(), this.getCurrentPosition().getY())) {
                    if (item.canWalkOn(this, room, null)) {
                        item.onWalkOn(this, room, new Object[]{this.getCurrentPosition(), next});
                    } else if (item instanceof ConditionalGate conditionalGate) {
                        this.setRotation(oldRotation);
                        this.decrementTilesMoved();
                        this.setGoalLocation(this.getCurrentPosition());
                        this.removeStatus(RoomUnitStatus.MOVE);
                        room.sendComposer(new UserUpdateComposer(this).compose());

                        if(this instanceof RoomHabbo) {
                            conditionalGate.onRejected(this, this.getRoom(), new Object[]{});
                        }

                        return false;
                    }
                } else {
                    item.onWalk(this, room, new Object[]{this.getCurrentPosition(), next});
                }

                zHeight += item.getZ();

                if (!item.getBaseItem().allowSit() && !item.getBaseItem().allowLay()) {
                    zHeight += Item.getCurrentHeight(item);
                }
            } else {
                zHeight += room.getLayout().getHeightAtSquare(next.getX(), next.getY());
            }


            this.setPreviousLocation(this.getCurrentPosition());

            this.setStatus(RoomUnitStatus.MOVE, next.getX() + "," + next.getY() + "," + zHeight);

            if(habbo != null) {
                RideablePet rideablePet = habbo.getHabboInfo().getRiding();

                if(rideablePet != null) {
                    RoomUnit ridingUnit = rideablePet.getRoomUnit();

                    if (ridingUnit != null) {
                        ridingUnit.setPreviousLocationZ(this.getCurrentZ());
                        this.setCurrentZ(zHeight - 1.0);
                        ridingUnit.setRotation(RoomRotation.values()[Rotation.Calculate(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), next.getX(), next.getY())]);
                        ridingUnit.setPreviousLocation(this.getCurrentPosition());
                        ridingUnit.setGoalLocation(this.getGoalLocation());
                        ridingUnit.setStatus(RoomUnitStatus.MOVE, next.getX() + "," + next.getY() + "," + (zHeight - 1.0));
                        room.sendComposer(new UserUpdateComposer(ridingUnit).compose());
                    }
                }
            }

            this.setCurrentZ(zHeight);
            this.setCurrentPosition(room.getLayout().getTile(next.getX(), next.getY()));

            if(this instanceof RoomHabbo roomHabbo) {
                roomHabbo.resetIdleTicks();
            }

            if(habbo != null) {
                RoomItem topItem = room.getRoomItemManager().getTopItemAt(next.getX(), next.getY());

                boolean isAtDoor = next.getX() == room.getLayout().getDoorX() && next.getY() == room.getLayout().getDoorY();
                boolean publicRoomKicks = !room.getRoomInfo().isPublicRoom() || Emulator.getConfig().getBoolean("hotel.room.public.doortile.kick");
                boolean invalidated = topItem != null && topItem.invalidatesToRoomKick();

                if (this.isCanLeaveRoomByDoor() && isAtDoor && publicRoomKicks && !invalidated) {
                    Emulator.getThreading().run(new RoomUnitKick(habbo, room, false), 500);
                }
            }

            return false;

        } catch (Exception e) {
            log.error("Caught exception", e);
            return false;
        }
    }

    public void setDance(DanceType danceType) {
        if (this.danceType != danceType) {
            boolean isDancing = !this.danceType.equals(DanceType.NONE);
            this.danceType = danceType;
            this.getRoom().sendComposer(new DanceMessageComposer(this).compose());

            if (danceType.equals(DanceType.NONE) && isDancing) {
                WiredHandler.handle(WiredTriggerType.STOPS_DANCING, this, this.getRoom(), new Object[]{this});
            } else if (!danceType.equals(DanceType.NONE) && !isDancing) {
                WiredHandler.handle(WiredTriggerType.STARTS_DANCING, this, this.getRoom(), new Object[]{this});
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
        } else {
            RoomAvatar roomAvatar = this;
        }

        if(this instanceof RoomHabbo) {
            Habbo habbo = this.getRoom().getRoomUnitManager().getHabboByRoomUnit(this);
            if(habbo == null || (habbo.getHabboInfo().isInGame() && !forceEffect)) {
                return;
            }
        }

        if (duration == -1 || duration == Integer.MAX_VALUE) {
            duration = Integer.MAX_VALUE;
        } else {
            duration += Emulator.getIntUnixTimestamp();
        }

        if ((this.getRoom().isAllowEffects() || forceEffect) && !this.isSwimming()) {
            this.setEffectId(effectId);
            this.setEffectEndTimestamp(duration);
            this.getRoom().sendComposer(new AvatarEffectMessageComposer(this).compose());
        }
    }

    private void handleSitStatus(RoomItem topItem) {
        if(!this.isCmdSitEnabled()) {
            if((topItem == null || !topItem.getBaseItem().allowSit()) && this.hasStatus(RoomUnitStatus.SIT)) {
                this.removeStatus(RoomUnitStatus.SIT);
                this.setStatusUpdateNeeded(true);
            } else if(this.getCurrentPosition().getState() == RoomTileState.SIT && (!this.hasStatus(RoomUnitStatus.SIT))) {
                this.setStatus(RoomUnitStatus.SIT, String.valueOf(Item.getCurrentHeight(topItem)));
                this.setStatusUpdateNeeded(true);
            }
        }
    }

    private void handleLayStatus(RoomItem topItem) {
        if(!this.isCmdLayEnabled()) {
            if((topItem == null || !topItem.getBaseItem().allowLay()) && this.hasStatus(RoomUnitStatus.LAY)) {
                this.removeStatus(RoomUnitStatus.LAY);
                this.setStatusUpdateNeeded(true);
            } else if(!this.hasStatus(RoomUnitStatus.LAY)) {
                this.setStatus(RoomUnitStatus.LAY, String.valueOf(Item.getCurrentHeight(topItem)));
                this.setStatusUpdateNeeded(true);
            }
        }
    }

    @Override
    public void clear() {
        super.clear();

        this.rideablePet = null;
        this.danceType = DanceType.NONE;
        this.handItem = 0;
        this.handItemTimestamp = 0;
        this.effectId = 0;
        this.effectEndTimestamp = -1;
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;
    }
}
