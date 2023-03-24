package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.items.interactions.InteractionWaterItem;
import com.eu.habbo.habbohotel.items.interactions.interfaces.ConditionalGate;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitSetGoalEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.RoomUnitKick;
import com.eu.habbo.util.pathfinding.Rotation;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RoomUnit {

    @Getter
    @Setter
    private boolean isWiredTeleporting = false;
    @Getter
    @Setter
    private boolean isLeavingTeleporter = false;
    @Getter
    @Setter
    private boolean isSwimming = false;
    @Getter
    private final ConcurrentHashMap<RoomUnitStatus, String> status;
    private final THashMap<String, Object> cacheable;
    @Getter
    @Setter
    private boolean canRotate = true;
    @Getter
    @Setter
    private boolean animateWalk = false;
    @Getter
    @Setter
    private boolean cmdTeleport = false;
    @Getter
    @Setter
    private boolean cmdSit = false;
    @Setter
    @Getter
    private boolean cmdStand = false;
    @Getter
    @Setter
    private boolean cmdLay = false;
    @Setter
    @Getter
    private boolean sitUpdate = false;
    @Getter
    @Setter
    private boolean isTeleporting = false;
    @Getter
    @Setter
    private boolean isKicked;
    @Getter
    @Setter
    private int kickCount = 0;
    private int id;
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
    private RoomTile currentLocation;
    @Getter
    private RoomTile goalLocation;
    @Getter
    private double z;
    private int tilesWalked;
    @Getter
    @Setter
    private boolean inRoom;
    @Setter
    @Accessors(chain = true)
    private boolean canWalk;
    @Setter
    @Getter
    private boolean fastWalk = false;
    private boolean statusUpdate = false;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean invisible = false;
    @Setter
    private boolean canLeaveRoomByDoor = true;
    @Setter
    private RoomUserRotation bodyRotation = RoomUserRotation.NORTH;
    @Getter
    @Setter
    private RoomUserRotation headRotation = RoomUserRotation.NORTH;
    @Getter
    @Setter
    private DanceType danceType;
    @Getter
    @Setter
    @Accessors(chain = true)
    private RoomUnitType roomUnitType;
    @Getter
    @Setter
    private Deque<RoomTile> path = new LinkedList<>();
    private int handItem;
    private long handItemTimestamp;
    private int walkTimeOut;
    private int effectId;
    private int effectEndTimestamp;
    private int previousEffectId;
    private int previousEffectEndTimestamp;
    private int timeInRoom;

    private int idleTimer;
    @Setter
    @Getter
    private Room room;
    @Getter
    @Setter
    private RoomRightLevels rightsLevel = RoomRightLevels.NONE;
    private final THashSet<Integer> overridableTiles;

    public RoomUnit() {
        this.id = 0;
        this.inRoom = false;
        this.canWalk = true;
        this.status = new ConcurrentHashMap<>();
        this.cacheable = new THashMap<>();
        this.roomUnitType = RoomUnitType.UNKNOWN;
        this.danceType = DanceType.NONE;
        this.handItem = 0;
        this.handItemTimestamp = 0;
        this.walkTimeOut = Emulator.getIntUnixTimestamp();
        this.effectId = 0;
        this.previousEffectId = 0;
        this.previousEffectEndTimestamp = -1;
        this.setKicked(false);
        this.overridableTiles = new THashSet<>();
        this.timeInRoom = 0;
    }

    public void clearWalking() {
        this.goalLocation = null;
        this.startLocation = this.currentLocation;
        this.inRoom = false;

        this.status.clear();

        this.cacheable.clear();
    }

    public void stopWalking() {
        synchronized (this.status) {
            this.status.remove(RoomUnitStatus.MOVE);
            this.setGoalLocation(this.currentLocation);
        }
    }

    public boolean cycle(Room room) {
        try {
            if (handleRider(room))
                return this.statusUpdate;

            Habbo habboT = room.getHabbo(this);
            if (!this.isWalking() && !this.isKicked() && this.status.remove(RoomUnitStatus.MOVE) == null && habboT != null) {
                habboT.getHabboInfo().getRiding().getRoomUnit().status.remove(RoomUnitStatus.MOVE);
                return true;
            }

            if (this.status.remove(RoomUnitStatus.SIT) != null || this.status.remove(RoomUnitStatus.MOVE) != null || this.status.remove(RoomUnitStatus.LAY) != null)
                this.statusUpdate = true;

            for (Map.Entry<RoomUnitStatus, String> set : this.status.entrySet()) {
                if (set.getKey().isRemoveWhenWalking()) {
                    this.status.remove(set.getKey());
                }
            }

            if (this.path == null || this.path.isEmpty())
                return true;

            boolean canfastwalk = habboT == null || habboT.getHabboInfo().getRiding() == null;

            RoomTile next = this.path.poll();
            boolean overrideChecks = next != null && this.canOverrideTile(next);

            if (this.path.isEmpty()) {
                this.setSitUpdate(true);

                if (next != null && next.hasUnits() && !overrideChecks) {
                    return false;
                }
            }

            Deque<RoomTile> peekPath = room.getLayout().findPath(this.currentLocation, this.path.peek(), this.goalLocation, this);

            if (peekPath == null) peekPath = new LinkedList<>();

            if (peekPath.size() >= 3) {
                if (path.isEmpty()) return true;

                path.pop();
                //peekPath.pop(); //Start
                peekPath.removeLast(); //End

                if (peekPath.peek() != next) {
                    next = peekPath.poll();
                    for (int i = 0; i < peekPath.size(); i++) {
                        this.path.addFirst(peekPath.removeLast());
                    }
                }
            }

            if (canfastwalk && this.fastWalk && this.path.size() > 1) {
                next = this.path.poll();
            }

            if (next == null)
                return true;

            Habbo habbo = room.getHabbo(this);

            this.status.remove(RoomUnitStatus.DEAD);

            if (habbo != null) {
                if (this.isIdle()) {
                    UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                    Emulator.getPluginManager().fireEvent(event);

                    if (!event.isCancelled() && !event.isIdle()) {
                        room.unIdle(habbo);
                        this.idleTimer = 0;
                    }
                }

                if (Emulator.getPluginManager().isRegistered(UserTakeStepEvent.class, false)) {
                    Event e = new UserTakeStepEvent(habbo, room.getLayout().getTile(this.getX(), this.getY()), next);
                    Emulator.getPluginManager().fireEvent(e);

                    if (e.isCancelled())
                        return true;
                }
            }

            HabboItem item = room.getTopItemAt(next.getX(), next.getY());

            double height = next.getStackHeight() - this.currentLocation.getStackHeight();
            if (!room.tileWalkable(next) || (!RoomLayout.ALLOW_FALLING && height < -RoomLayout.MAXIMUM_STEP_HEIGHT) || (next.getState() == RoomTileState.OPEN && height > RoomLayout.MAXIMUM_STEP_HEIGHT)) {
                this.room = room;
                this.path.clear();
                this.findPath();

                if (this.path.isEmpty()) {
                    this.status.remove(RoomUnitStatus.MOVE);
                    return false;
                }
                next = this.path.pop();

            }

            boolean canSitNextTile = room.canSitAt(next.getX(), next.getY());

            if (canSitNextTile) {
                HabboItem tallestChair = room.getTallestChair(next);

                if (tallestChair != null)
                    item = tallestChair;
            }

            if (next.equals(this.goalLocation) && next.getState() == RoomTileState.SIT && !overrideChecks
                    && (item == null || item.getZ() - this.getZ() > RoomLayout.MAXIMUM_STEP_HEIGHT)) {
                this.status.remove(RoomUnitStatus.MOVE);
                return false;
            }

            double zHeight = 0.0D;
            if (habbo != null && habbo.getHabboInfo().getRiding() != null) {
                zHeight += 1.0D;
            }

            HabboItem habboItem = room.getTopItemAt(this.getX(), this.getY());
            if (habboItem != null && (habboItem != item || !RoomLayout.pointInSquare(habboItem.getX(), habboItem.getY(), habboItem.getX() + habboItem.getBaseItem().getWidth() - 1, habboItem.getY() + habboItem.getBaseItem().getLength() - 1, next.getX(), next.getY())))
                habboItem.onWalkOff(this, room, new Object[]{this.getCurrentLocation(), next});


            this.tilesWalked++;

            RoomUserRotation oldRotation = this.getBodyRotation();
            this.setRotation(RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), next.getX(), next.getY())]);
            if (item != null) {
                if (item != habboItem || !RoomLayout.pointInSquare(item.getX(), item.getY(), item.getX() + item.getBaseItem().getWidth() - 1, item.getY() + item.getBaseItem().getLength() - 1, this.getX(), this.getY())) {
                    if (item.canWalkOn(this, room, null)) {
                        item.onWalkOn(this, room, new Object[]{this.getCurrentLocation(), next});
                    } else if (item instanceof ConditionalGate conditionalGate) {
                        this.setRotation(oldRotation);
                        this.tilesWalked--;
                        this.setGoalLocation(this.currentLocation);
                        this.status.remove(RoomUnitStatus.MOVE);
                        room.sendComposer(new UserUpdateComposer(this).compose());

                        if (habbo != null) {
                            conditionalGate.onRejected(this, this.getRoom(), new Object[]{});
                        }
                        return false;
                    }
                } else {
                    item.onWalk(this, room, new Object[]{this.getCurrentLocation(), next});
                }

                zHeight += item.getZ();

                if (!item.getBaseItem().allowSit() && !item.getBaseItem().allowLay()) {
                    zHeight += Item.getCurrentHeight(item);
                }
            } else {
                zHeight += room.getLayout().getHeightAtSquare(next.getX(), next.getY());
            }


            this.setPreviousLocation(this.getCurrentLocation());

            this.setStatus(RoomUnitStatus.MOVE, next.getX() + "," + next.getY() + "," + zHeight);
            if (habbo != null && habbo.getHabboInfo().getRiding() != null) {
                RoomUnit ridingUnit = habbo.getHabboInfo().getRiding().getRoomUnit();

                if (ridingUnit != null) {
                    ridingUnit.setPreviousLocationZ(this.getZ());
                    this.setZ(zHeight - 1.0);
                    ridingUnit.setRotation(RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), next.getX(), next.getY())]);
                    ridingUnit.setPreviousLocation(this.getCurrentLocation());
                    ridingUnit.setGoalLocation(this.getGoalLocation());
                    ridingUnit.setStatus(RoomUnitStatus.MOVE, next.getX() + "," + next.getY() + "," + (zHeight - 1.0));
                    room.sendComposer(new UserUpdateComposer(ridingUnit).compose());
                }
            }

            this.setZ(zHeight);
            this.setCurrentLocation(room.getLayout().getTile(next.getX(), next.getY()));
            this.resetIdleTimer();

            if (habbo != null) {
                HabboItem topItem = room.getTopItemAt(next.getX(), next.getY());

                boolean isAtDoor = next.getX() == room.getLayout().getDoorX() && next.getY() == room.getLayout().getDoorY();
                boolean publicRoomKicks = !room.isPublicRoom() || Emulator.getConfig().getBoolean("hotel.room.public.doortile.kick");
                boolean invalidated = topItem != null && topItem.invalidatesToRoomKick();

                if (this.canLeaveRoomByDoor && isAtDoor && publicRoomKicks && !invalidated) {
                    Emulator.getThreading().run(new RoomUnitKick(habbo, room, false), 500);
                }
            }

            return false;

        } catch (Exception e) {
            log.error("Caught exception", e);
            return false;
        }
    }

    private boolean handleRider(Room room) {
        Habbo rider = null;
        if (this.getRoomUnitType() == RoomUnitType.PET) {
            Pet pet = room.getPet(this);
            if (pet instanceof RideablePet rideablePet) {
                rider = rideablePet.getRider();
            }
        }

        if (rider != null) {
            // copy things from rider
            if (this.status.containsKey(RoomUnitStatus.MOVE) && !rider.getRoomUnit().getStatus().containsKey(RoomUnitStatus.MOVE)) {
                this.status.remove(RoomUnitStatus.MOVE);
            }

            if (rider.getRoomUnit().getCurrentLocation().getX() != this.getX() || rider.getRoomUnit().getCurrentLocation().getY() != this.getY()) {
                this.status.put(RoomUnitStatus.MOVE, rider.getRoomUnit().getCurrentLocation().getX() + "," + rider.getRoomUnit().getCurrentLocation().getY() + "," + (rider.getRoomUnit().getCurrentLocation().getStackHeight()));
                this.setPreviousLocation(rider.getRoomUnit().getPreviousLocation());
                this.setPreviousLocationZ(rider.getRoomUnit().getPreviousLocation().getStackHeight());
                this.setCurrentLocation(rider.getRoomUnit().getCurrentLocation());
                this.setZ(rider.getRoomUnit().getCurrentLocation().getStackHeight());
            }

            return true;
        }
        return false;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RoomTile getCurrentLocation() {
        return this.currentLocation;
    }

    public void setCurrentLocation(RoomTile location) {
        if (location != null) {
            if (this.currentLocation != null) {
                this.currentLocation.removeUnit(this);
            }
            this.currentLocation = location;
            location.addUnit(this);
        }
    }

    public short getX() {
        return this.currentLocation.getX();
    }

    public short getY() {
        return this.currentLocation.getY();
    }

    public void setZ(double z) {
        this.z = z;

        if (this.room != null) {
            Bot bot = this.room.getBot(this);
            if (bot != null) {
                bot.needsUpdate(true);
            }
        }
    }

    public void setRotation(RoomUserRotation rotation) {
        this.bodyRotation = rotation;
        this.headRotation = rotation;
    }

    public RoomUserRotation getBodyRotation() {
        return this.bodyRotation;
    }

    public boolean canWalk() {
        return this.canWalk;
    }

    public int tilesWalked() {
        return this.tilesWalked;
    }

    public RoomUnit setGoalLocation(RoomTile goalLocation) {
        if (goalLocation != null) {
            this.setGoalLocation(goalLocation, false);
        }
        return this;
    }

    public void setGoalLocation(RoomTile goalLocation, boolean noReset) {
        if (Emulator.getPluginManager().isRegistered(RoomUnitSetGoalEvent.class, false)) {
            Event event = new RoomUnitSetGoalEvent(this.room, this, goalLocation);
            Emulator.getPluginManager().fireEvent(event);

            if (event.isCancelled())
                return;
        }

        /// Set start location
        this.startLocation = this.currentLocation;

        if (goalLocation != null && !noReset) {
            boolean isWalking = this.hasStatus(RoomUnitStatus.MOVE);
            this.goalLocation = goalLocation;
            this.findPath(); ///< Quadral: this is where we start formulating a path
            if (!this.path.isEmpty()) {
                this.tilesWalked = isWalking ? this.tilesWalked : 0;
                this.setCmdSit(false);
            } else {
                this.goalLocation = this.currentLocation;
            }
        }
    }

    public RoomUnit setLocation(RoomTile location) {
        if (location != null) {
            this.startLocation = location;
            setPreviousLocation(location);
            setCurrentLocation(location);
            this.goalLocation = location;
            this.botStartLocation = location;
        }
        return this;
    }

    public void setPreviousLocation(RoomTile previousLocation) {
        this.previousLocation = previousLocation;
        this.previousLocationZ = this.z;
    }

    public RoomUnit setPathFinderRoom(Room room) {
        this.room = room;
        return this;
    }

    public void findPath() {
        if (this.room != null && this.room.getLayout() != null && this.goalLocation != null && (this.goalLocation.isWalkable() || this.room.canSitOrLayAt(this.goalLocation.getX(), this.goalLocation.getY()) || this.canOverrideTile(this.goalLocation))) {
            Deque<RoomTile> newPath = this.room.getLayout().findPath(this.currentLocation, this.goalLocation, this.goalLocation, this);
            if (newPath != null) this.path = newPath;
        }
    }

    public boolean isAtGoal() {
        return this.currentLocation.equals(this.goalLocation);
    }

    public boolean isWalking() {
        return !this.isAtGoal() && this.canWalk;
    }

    public String getStatus(RoomUnitStatus key) {
        return this.status.get(key);
    }

    public RoomUnit removeStatus(RoomUnitStatus key) {
        this.status.remove(key);
        return this;
    }

    public void setStatus(RoomUnitStatus key, String value) {
        if (key != null && value != null) {
            this.status.put(key, value);
        }
    }

    public boolean hasStatus(RoomUnitStatus key) {
        return this.status.containsKey(key);
    }

    public void clearStatus() {
        this.status.clear();
    }

    public void statusUpdate(boolean update) {
        this.statusUpdate = update;
    }

    public boolean needsStatusUpdate() {
        return this.statusUpdate;
    }

    public TMap<String, Object> getCacheable() {
        return this.cacheable;
    }

    public int getHandItem() {
        return this.handItem;
    }

    public RoomUnit setHandItem(int handItem) {
        this.handItem = handItem;
        this.handItemTimestamp = System.currentTimeMillis();
        return this;
    }

    public long getHandItemTimestamp() {
        return this.handItemTimestamp;
    }

    public int getEffectId() {
        return this.effectId;
    }


    public void setEffectId(int effectId, int endTimestamp) {
        this.effectId = effectId;
        this.effectEndTimestamp = endTimestamp;
    }

    public int getEffectEndTimestamp() {
        return this.effectEndTimestamp;
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

    public void increaseIdleTimer() {
        this.idleTimer++;
    }

    public boolean isIdle() {
        return this.idleTimer > Room.IDLE_CYCLES; //Amount of room cycles / 2 = seconds.
    }

    public int getIdleTimer() {
        return this.idleTimer;
    }

    public void resetIdleTimer() {
        this.idleTimer = 0;
    }

    public void setIdle() {
        this.idleTimer = Room.IDLE_CYCLES + 1;
    }

    public void lookAtPoint(RoomTile location) {
        if (!this.isCanRotate()) return;

        if (Emulator.getPluginManager().isRegistered(RoomUnitLookAtPointEvent.class, false)) {
            Event lookAtPointEvent = new RoomUnitLookAtPointEvent(this.room, this, location);
            Emulator.getPluginManager().fireEvent(lookAtPointEvent);

            if (lookAtPointEvent.isCancelled())
                return;
        }

        if (this.status.containsKey(RoomUnitStatus.LAY)) {
            return;
        }

        if (!this.status.containsKey(RoomUnitStatus.SIT)) {
            this.bodyRotation = (RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), location.getX(), location.getY())]);
        }

        RoomUserRotation rotation = (RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), location.getX(), location.getY())]);

        if (Math.abs(rotation.getValue() - this.bodyRotation.getValue()) <= 1) {
            this.headRotation = rotation;
        }
    }

    public boolean canOverrideTile(RoomTile tile) {
        if (tile == null || room == null || room.getLayout() == null) return false;

        if (room.getItemsAt(tile).stream().anyMatch(i -> i.canOverrideTile(this, room, tile)))
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
        if (room == null || room.getLayout() == null) return;

        int tileIndex = (tile.getX() & 0xFF) | (tile.getY() << 12);
        this.overridableTiles.remove(tileIndex);
    }

    public void clearOverrideTiles() {
        this.overridableTiles.clear();
    }

    public boolean canLeaveRoomByDoor() {
        return canLeaveRoomByDoor;
    }

    public boolean canForcePosture() {
        if (this.room == null) return false;

        HabboItem topItem = this.room.getTopItemAt(this.getX(), this.getY());

        return (!(topItem instanceof InteractionWater) && !(topItem instanceof InteractionWaterItem));
    }

    public RoomTile getClosestTile(List<RoomTile> tiles) {
        return tiles.stream().min(Comparator.comparingDouble(a -> a.distance(this.getCurrentLocation()))).orElse(null);
    }

    public RoomTile getClosestAdjacentTile(short x, short y, boolean diagonal) {
        if (room == null) return null;

        RoomTile baseTile = room.getLayout().getTile(x, y);

        if (baseTile == null) return null;

        List<Integer> rotations = new ArrayList<>();
        rotations.add(RoomUserRotation.SOUTH.getValue());
        rotations.add(RoomUserRotation.NORTH.getValue());
        rotations.add(RoomUserRotation.EAST.getValue());
        rotations.add(RoomUserRotation.WEST.getValue());

        if (diagonal) {
            rotations.add(RoomUserRotation.NORTH_EAST.getValue());
            rotations.add(RoomUserRotation.NORTH_WEST.getValue());
            rotations.add(RoomUserRotation.SOUTH_EAST.getValue());
            rotations.add(RoomUserRotation.SOUTH_WEST.getValue());
        }

        return this.getClosestTile(
                rotations.stream()
                        .map(rotation -> room.getLayout().getTileInFront(baseTile, rotation))
                        .filter(t -> t != null && t.isWalkable() && (this.getCurrentLocation().equals(t)
                                || !room.hasHabbosAt(t.getX(), t.getY())))
                        .toList()
        );
    }

}
