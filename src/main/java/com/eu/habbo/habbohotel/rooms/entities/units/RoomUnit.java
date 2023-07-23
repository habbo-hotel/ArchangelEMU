package com.eu.habbo.habbohotel.rooms.entities.units;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.items.interactions.InteractionWaterItem;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomEntity;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitSetGoalEvent;
import com.eu.habbo.util.pathfinding.Rotation;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Accessors(chain = true)
public abstract class RoomUnit extends RoomEntity {
    @Setter
    private int virtualId;
    @Setter
    private RoomUnitType roomUnitType;
    @Setter
    private RoomRotation bodyRotation;
    @Setter
    private RoomRotation headRotation;
    @Setter
    private boolean canWalk;
    @Setter
    private boolean canRotate;
    @Setter
    private boolean isTeleporting;
    @Setter
    private boolean cmdTeleportEnabled = false;
    @Setter
    private boolean cmdSitEnabled = false;
    @Setter
    private boolean cmdStandEnabled = false;
    @Setter
    private boolean cmdLayEnabled = false;
    @Setter
    private boolean isSwimming = false;
    @Setter
    private boolean fastWalkEnabled;
    private final ConcurrentHashMap<RoomUnitStatus, String> statuses;
    @Setter
    private boolean statusUpdateNeeded;
    @Getter
    @Setter
    private boolean isWiredTeleporting = false;
    @Getter
    @Setter
    private boolean isLeavingTeleporter = false;
    private final THashMap<String, Object> cacheable;
    @Getter
    @Setter
    private boolean animateWalk = false;
    @Setter
    @Getter
    private boolean sitUpdate = false;
    @Setter
    private boolean isKicked;
    @Setter
    private int kickCount = 0;
    @Getter
    private RoomTile startLocation;
    @Getter
    @Setter
    private RoomTile botStartLocation;
    @Getter
    private RoomTile previousLocation;
    @Getter
    @Setter
    @Accessors(chain = true)
    private double previousLocationZ;
    @Getter
    private RoomTile goalLocation;
    @Getter
    @Setter
    private boolean inRoom;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean invisible = false;
    @Setter
    private boolean canLeaveRoomByDoor = true;
    private int walkTimeOut;
    private int previousEffectId;
    private int previousEffectEndTimestamp;
    private int timeInRoom;
    @Getter
    private RoomRightLevels rightsLevel = RoomRightLevels.NONE;
    private final THashSet<Integer> overridableTiles;

    public RoomUnit() {
        this.virtualId = 0;
        this.bodyRotation = RoomRotation.NORTH;
        this.headRotation = RoomRotation.NORTH;
        this.canWalk = true;
        this.canRotate = true;
        this.isTeleporting = false;
        this.fastWalkEnabled = false;
        this.statuses = new ConcurrentHashMap<>();
        this.statusUpdateNeeded = false;

        this.inRoom = false;
        this.cacheable = new THashMap<>();
        this.roomUnitType = RoomUnitType.UNKNOWN;
        this.walkTimeOut = Emulator.getIntUnixTimestamp();
        this.timeInRoom = 0;

        //RoomAvatar
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;

        //RoomHabbo
        this.isKicked = false;
        this.overridableTiles = new THashSet<>();
    }

    public abstract boolean cycle(Room room);

    @Override
    public RoomUnit setCurrentPosition(RoomTile tile) {
        if (this.getCurrentPosition() != null) {
            this.getCurrentPosition().removeUnit(this);
        }

        super.setCurrentPosition(tile);

        if(this.getCurrentPosition() != null) {
            tile.addRoomUnit(this);
        }

        return this;
    }

    public void setRotation(RoomRotation rotation) {
        this.bodyRotation = rotation;
        this.headRotation = rotation;
        this.statusUpdateNeeded = true;
    }

    public void clearWalking() {
        this.goalLocation = null;
        this.setCurrentPosition(null);
        this.startLocation = this.getCurrentPosition();
        this.statuses.clear();
        this.cacheable.clear();
    }

    public void stopWalking() {
        synchronized (this.statuses) {
            this.statuses.remove(RoomUnitStatus.MOVE);
            this.statusUpdateNeeded = true;
            this.setGoalLocation(this.getCurrentPosition());
        }
    }

    @Override
    public RoomUnit setCurrentZ(double currentZ) {
        super.setCurrentZ(currentZ);

        if (this.getRoom() != null) {
            Bot bot = this.getRoom().getRoomUnitManager().getRoomBotById(getVirtualId());
            if (bot != null) {
                bot.needsUpdate(true);
            }
        }

        return this;
    }

    public RoomUnit setGoalLocation(RoomTile goalLocation) {
        if (goalLocation != null) {
            this.setGoalLocation(goalLocation, false);
        }
        return this;
    }

    public void setGoalLocation(RoomTile goalLocation, boolean noReset) {
        if (Emulator.getPluginManager().isRegistered(RoomUnitSetGoalEvent.class, false)) {
            Event event = new RoomUnitSetGoalEvent(this.getRoom(), this, goalLocation);
            Emulator.getPluginManager().fireEvent(event);

            if (event.isCancelled())
                return;
        }

        /// Set start location
        this.startLocation = this.getCurrentPosition();

        if (goalLocation != null && !noReset) {
            boolean isWalking = this.hasStatus(RoomUnitStatus.MOVE);
            this.goalLocation = goalLocation;
            this.findPath(); ///< Quadral: this is where we start formulating a path
            if (!this.getPath().isEmpty()) {
                this.setTilesMoved(isWalking ? this.getTilesMoved() : 0);
                this.setCmdSitEnabled(false);
            } else {
                this.goalLocation = this.getCurrentPosition();
            }
        }
    }

    public RoomUnit setLocation(RoomTile location) {
        if (location != null) {
            this.startLocation = location;
            this.setPreviousLocation(location);
            this.setCurrentPosition(location);
            this.goalLocation = location;
            this.botStartLocation = location;
        }
        return this;
    }

    public void setPreviousLocation(RoomTile previousLocation) {
        this.previousLocation = previousLocation;
        this.previousLocationZ = this.getCurrentZ();
    }

    public void findPath() {
        if (this.getRoom() != null && this.getRoom().getLayout() != null && this.goalLocation != null && (this.goalLocation.isWalkable() || this.getRoom().canSitOrLayAt(this.goalLocation.getX(), this.goalLocation.getY()) || this.canOverrideTile(this.goalLocation))) {
            Deque<RoomTile> newPath = this.getRoom().getLayout().findPath(this.getCurrentPosition(), this.goalLocation, this.goalLocation, this);
            if (newPath != null) this.setPath(newPath);
        }
    }

    public boolean isAtGoal() {
        if(this.getCurrentPosition() == null) {
            return false;
        }

        return this.getCurrentPosition().equals(this.goalLocation);
    }

    public boolean isWalking() {
        return !this.isAtGoal() && this.canWalk;
    }

    public String getStatus(RoomUnitStatus key) {
        return this.statuses.get(key);
    }

    public RoomUnit removeStatus(RoomUnitStatus key) {
        this.statuses.remove(key);
        this.statusUpdateNeeded = true;
        return this;
    }

    public void setStatus(RoomUnitStatus key, String value) {
        if (key != null && value != null) {
            this.statuses.put(key, value);
            this.statusUpdateNeeded = true;
        }
    }

    public void setRightsLevel(RoomRightLevels rightsLevel) {
        this.rightsLevel = rightsLevel;
        this.statusUpdateNeeded = true;
    }

    public boolean hasStatus(RoomUnitStatus key) {
        return this.statuses.containsKey(key);
    }

    public void makeStand() {
        RoomItem item = this.getRoom().getRoomItemManager().getTopItemAt(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());
        if (item == null || !item.getBaseItem().allowSit() || !item.getBaseItem().allowLay()) {
            this.setCmdStandEnabled(true);
            this.setBodyRotation(RoomRotation.values()[this.getBodyRotation().getValue() - this.getBodyRotation().getValue() % 2]);
            this.removeStatus(RoomUnitStatus.SIT);
            this.getRoom().sendComposer(new UserUpdateComposer(this).compose());
        }
    }

    public void makeSit() {
        if (this.hasStatus(RoomUnitStatus.SIT) || !this.canForcePosture()) {
            return;
        }

        this.setCmdSitEnabled(true);
        this.setBodyRotation(RoomRotation.values()[this.getBodyRotation().getValue() - this.getBodyRotation().getValue() % 2]);
        this.setStatus(RoomUnitStatus.SIT, 0.5 + "");

        if(this instanceof RoomAvatar roomAvatar) {
            roomAvatar.setDance(DanceType.NONE);
        }

        this.getRoom().sendComposer(new UserUpdateComposer(this).compose());
    }

    public void clearStatuses() {
        this.statuses.clear();
    }

    public TMap<String, Object> getCacheable() {
        return this.cacheable;
    }

    public int getPreviousEffectId() {
        return this.previousEffectId;
    }

    public void setPreviousEffectId(int effectId, int endTimestamp) {
        this.previousEffectId = effectId;
        this.previousEffectEndTimestamp = endTimestamp;
    }

    public int getPreviousEffectEndTimestamp() {
        return this.previousEffectEndTimestamp;
    }

    public int getWalkTimeOut() {
        return this.walkTimeOut;
    }

    public void setWalkTimeOut(int walkTimeOut) {
        this.walkTimeOut = walkTimeOut;
    }

    public void increaseTimeInRoom() {
        this.timeInRoom++;
    }

    public int getTimeInRoom() {
        return this.timeInRoom;
    }

    public void resetTimeInRoom() {
        this.timeInRoom = 0;
    }

    public void lookAtPoint(RoomTile location) {
        if (!this.isCanRotate()) {
            return;
        }

        if (Emulator.getPluginManager().isRegistered(RoomUnitLookAtPointEvent.class, false)) {
            Event lookAtPointEvent = new RoomUnitLookAtPointEvent(this.getRoom(), this, location);
            Emulator.getPluginManager().fireEvent(lookAtPointEvent);

            if (lookAtPointEvent.isCancelled())
                return;
        }

        if (this.statuses.containsKey(RoomUnitStatus.LAY)) {
            return;
        }

        RoomRotation rotation = (RoomRotation.values()[Rotation.Calculate(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), location.getX(), location.getY())]);

        if (!this.statuses.containsKey(RoomUnitStatus.SIT)) {
            this.bodyRotation = rotation;
        }

        if (Math.abs(rotation.getValue() - this.bodyRotation.getValue()) <= 1) {
            this.headRotation = rotation;
        }

        this.statusUpdateNeeded = true;
    }

    public boolean canOverrideTile(RoomTile tile) {
        if (tile == null || this.getRoom() == null || this.getRoom().getLayout() == null) return false;

        if (this.getRoom().getRoomItemManager().getItemsAt(tile).stream().anyMatch(i -> i.canOverrideTile(this, this.getRoom(), tile)))
            return true;

        int tileIndex = (tile.getX() & 0xFF) | (tile.getY() << 12);
        return this.overridableTiles.contains(tileIndex);
    }

    public void addOverrideTile(RoomTile tile) {
        int tileIndex = (tile.getX() & 0xFF) | (tile.getY() << 12);
        if (!this.overridableTiles.contains(tileIndex)) {
            this.overridableTiles.add(tileIndex);
        }
    }

    public void removeOverrideTile(RoomTile tile) {
        if (this.getRoom() == null || this.getRoom().getLayout() == null) return;

        int tileIndex = (tile.getX() & 0xFF) | (tile.getY() << 12);
        this.overridableTiles.remove(tileIndex);
    }

    public boolean canForcePosture() {
        if (this.getRoom() == null) return false;

        RoomItem topItem = this.getRoom().getRoomItemManager().getTopItemAt(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());

        return (!(topItem instanceof InteractionWater) && !(topItem instanceof InteractionWaterItem));
    }

    public RoomTile getClosestTile(List<RoomTile> tiles) {
        return tiles.stream().min(Comparator.comparingDouble(a -> a.distance(this.getCurrentPosition()))).orElse(null);
    }

    public RoomTile getClosestAdjacentTile(short x, short y, boolean diagonal) {
        if (this.getRoom() == null) return null;

        RoomTile baseTile = this.getRoom().getLayout().getTile(x, y);

        if (baseTile == null) return null;

        List<Integer> rotations = new ArrayList<>();
        rotations.add(RoomRotation.SOUTH.getValue());
        rotations.add(RoomRotation.NORTH.getValue());
        rotations.add(RoomRotation.EAST.getValue());
        rotations.add(RoomRotation.WEST.getValue());

        if (diagonal) {
            rotations.add(RoomRotation.NORTH_EAST.getValue());
            rotations.add(RoomRotation.NORTH_WEST.getValue());
            rotations.add(RoomRotation.SOUTH_EAST.getValue());
            rotations.add(RoomRotation.SOUTH_WEST.getValue());
        }

        return this.getClosestTile(
                rotations.stream()
                        .map(rotation -> this.getRoom().getLayout().getTileInFront(baseTile, rotation))
                        .filter(t -> t != null && t.isWalkable() && (this.getCurrentPosition().equals(t) || !this.getRoom().getRoomUnitManager().hasHabbosAt(t)))
                        .toList()
        );
    }

    public void clear() {
        this.setRoom(null);
        this.canWalk = true;
        this.canRotate = true;
        this.fastWalkEnabled = false;
        this.cmdTeleportEnabled = false;
        this.clearStatuses();
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;
        this.isKicked = false;
    }
}
