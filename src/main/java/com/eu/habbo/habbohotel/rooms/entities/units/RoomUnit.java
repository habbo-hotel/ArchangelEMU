package com.eu.habbo.habbohotel.rooms.entities.units;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.items.interactions.InteractionWaterItem;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitManager;
import com.eu.habbo.habbohotel.rooms.constants.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomEntity;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.rooms.items.RoomItemManager;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.units.Unit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitSetGoalEvent;
import com.eu.habbo.roleplay.messages.incoming.controls.MovementDirection;
import com.eu.habbo.util.pathfinding.Rotation;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Accessors(chain = true)
public abstract class RoomUnit extends RoomEntity {
    @Setter
    protected int virtualId;
    @Setter
    protected Unit unit;
    @Setter
    protected RoomUnitType roomUnitType;
    @Setter
    protected RoomRotation bodyRotation;
    @Setter
    protected RoomRotation headRotation;
    @Setter
    protected boolean canWalk;
    @Setter
    protected boolean canRotate;
    @Setter
    protected boolean isTeleporting;
    @Setter
    protected boolean cmdSitEnabled = false;
    @Setter
    protected boolean cmdStandEnabled = false;
    @Setter
    protected boolean cmdLayEnabled = false;
    @Setter
    protected boolean isSwimming = false;
    @Setter
    protected boolean cmdFastWalkEnabled;
    @Setter
    protected boolean temporalFastWalkEnabled;
    protected final ConcurrentHashMap<RoomUnitStatus, String> statuses;
    @Setter
    protected boolean statusUpdateNeeded;
    @Setter
    protected boolean isWiredTeleporting = false;
    @Setter
    protected boolean isLeavingTeleporter = false;
    protected final THashMap<String, Object> cacheable;
    @Setter
    protected boolean isKicked;
    @Setter
    protected int kickCount = 0;
    @Setter
    @Accessors(chain = true)
    protected boolean invisible = false;
    @Setter
    protected boolean canLeaveRoomByDoor = true;
    @Setter
    protected int walkTimeOut;
    protected int timeInRoom;
    protected RoomRightLevels rightsLevel = RoomRightLevels.NONE;
    protected final THashSet<Integer> overridableTiles;

    public RoomUnit() {
        this.virtualId = 0;
        this.bodyRotation = RoomRotation.NORTH;
        this.headRotation = RoomRotation.NORTH;
        this.canWalk = true;
        this.canRotate = true;
        this.isTeleporting = false;
        this.cmdFastWalkEnabled = false;
        this.statuses = new ConcurrentHashMap<>();
        this.statusUpdateNeeded = false;

        this.cacheable = new THashMap<>();
        this.roomUnitType = RoomUnitType.UNKNOWN;
        this.walkTimeOut = Emulator.getIntUnixTimestamp();
        this.timeInRoom = 0;

        //RoomHabbo
        this.isKicked = false;
        this.overridableTiles = new THashSet<>();
        this.statusUpdateNeeded = false;
    }

    public RoomItem getCurrentItem() {
        if (this.room == null) {
            return null;
        }
        return this.room.getRoomItemManager().getTopItemAt(this.currentPosition);
    }

    public void cycle() {
        if(this.isWalking()) {
            this.processWalking();

        } else {
            this.stopWalking();
        }
    };

    public boolean isInRoom() {
        return this.room != null;
    }

    @Override
    public RoomUnit setCurrentPosition(RoomTile tile) {
        super.setCurrentPosition(tile);

        if (this.previousPosition != null) {
            this.previousPosition.removeUnit(this);
        }

        if(this.currentPosition != null) {
            tile.addRoomUnit(this);
        }

        return this;
    }

    public void setRotation(RoomRotation rotation) {
        this.bodyRotation = rotation;
        this.headRotation = rotation;
        this.statusUpdateNeeded = true;
    }

    private Timer movementTimer;
    private MovementDirection currentDirection;

    public synchronized void startMoving(MovementDirection direction) {
        stopMoving(); // Stop any previous movement

        this.currentDirection = direction;

        movementTimer = new Timer();
        movementTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean canContinue = moveInDirection(direction);
                if (!canContinue) {
                    stopMoving();
                }
            }
        }, 0, 25);
    }

    public synchronized void stopMoving() {
        if (movementTimer != null) {
            movementTimer.cancel();
            movementTimer = null;
        }
        this.currentDirection = null;
        stopWalking(); // Ensure walking stops when movement stops
    }

    private synchronized boolean moveInDirection(MovementDirection direction) {
        RoomTile currentPosition = this.getCurrentPosition();

        RoomTile targetTile = switch (direction) {
            case UP -> this.getRoom().getLayout().getTileInFront(currentPosition, 0);
            case LEFT -> this.getRoom().getLayout().getTileInFront(currentPosition, 6);
            case DOWN -> this.getRoom().getLayout().getTileInFront(currentPosition, 4);
            case RIGHT -> this.getRoom().getLayout().getTileInFront(currentPosition, 2);
            default -> null;
        };

        if (targetTile != null && targetTile.isWalkable()) {
            this.walkTo(targetTile);
            return true;
        } else {
            return false; // Stop if not walkable
        }
    }

    public synchronized void stopWalking() {
        synchronized (this.statuses) {
            if (this.path != null) {
                this.path.clear();
            }

            this.nextPosition = null;
            this.targetPosition = null;

            this.removeStatus(RoomUnitStatus.MOVE);
            this.handleSitStatus();
            this.handleLayStatus();

            this.temporalFastWalkEnabled = false;

            RoomItemManager roomItemManager = this.room.getRoomItemManager();
            RoomUnitManager roomUnitManager = this.room.getRoomUnitManager();

            roomItemManager.getItemsAt(this.currentPosition)
                    .stream()
                    .findFirst()
                    .ifPresent(item -> {
                        roomUnitManager.getRoomUnitsAt(this.currentPosition)
                                .stream()
                                .findFirst()
                                .ifPresent(unit -> {
                                    try {
                                        item.onWalkOn(unit, this.room, null);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    });
        }
    }

    @Override
    public RoomUnit setCurrentZ(double currentZ) {
        super.setCurrentZ(currentZ);

        if (this.room != null) {
            Bot bot = this.room.getRoomUnitManager().getRoomBotManager().getRoomBotById(getVirtualId());
            if (bot != null) {
                bot.setSqlUpdateNeeded(true);
            }
        }

        this.statusUpdateNeeded = true;
        return this;
    }

    /**
     * Sets the target position for the character's movement and calculates the path to reach the destination.
     *
     * @param goalLocation The target location (represented by a {@link RoomTile}) to which the character should move.
     * @return {@code true} if the path calculation to the goal location is successful, {@code false} otherwise.
     *         Returns {@code false} if the goal location is not walkable or the character's current room does not allow sitting or laying at that location.
     *         Additionally, the path calculation may be canceled by registered plugins listening to {@link RoomUnitSetGoalEvent}.
     *         In such cases, the method also returns {@code false}.
    */
    public boolean walkTo(RoomTile goalLocation) {
        if(this.canWalk && !goalLocation.isWalkable() && !this.room.canSitOrLayAt(goalLocation)) {
            return false;
        }

        if (Emulator.getPluginManager().isRegistered(RoomUnitSetGoalEvent.class, false)) {
            Event event = new RoomUnitSetGoalEvent(this.room, this, goalLocation);
            Emulator.getPluginManager().fireEvent(event);

            if (event.isCancelled())
                return false;
        }

        if(this.nextPosition != null) {
            this.setCurrentPosition(this.nextPosition);
            this.setCurrentZ(this.nextZ);
        }

        this.targetPosition = goalLocation;
        this.findPath();
        return true;
    }

    public RoomUnit setLocation(RoomTile location) {
        if (location == null) {
            return this;
        }

        this.setCurrentPosition(location);
        this.targetPosition = location;

        if (this.room == null) {
            return this;
        }

        RoomItemManager roomItemManager = this.room.getRoomItemManager();
        RoomUnitManager roomUnitManager = this.room.getRoomUnitManager();

        roomItemManager.getItemsAt(location)
                .stream()
                .findFirst()
                .ifPresent(item -> {
                    roomUnitManager.getRoomUnitsAt(location)
                            .stream()
                            .findFirst()
                            .ifPresent(unit -> {
                                try {
                                    item.onWalkOn(unit, this.room, null);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });

        return this;
    }

    /**
     * Finds a path from the current position to the target position within a room's layout, if valid.
     * The method checks if the room, layout, and target position are valid, and if the target position is walkable
     * or can be occupied by sitting or laying, or if it can be overridden.
     * If all conditions are met, the method attempts to find a path from the current position to the target position
     * using the room's layout and sets the path accordingly.
     *
     * Pre-conditions:
     *  - The current object must have a valid room set (using the `setRoom(Room room)` method).
     *  - The target position must be set (using the `setTargetPosition(RoomTile targetPosition)` method).
     *
     * Post-conditions:
     *  - If a valid path is found from the current position to the target position, the path is set using the `setPath(Deque<RoomTile> path)` method.
     *
     * Note:
     *  - The method relies on the validity of the room and layout, and whether the target position is walkable, or can be occupied by sitting or laying,
     *    or can be overridden. If any of these conditions are not met, the method will not attempt to find a path.
     *  - The `findPath()` method assumes that the room and layout are well-defined and consistent, and that the target position is within the boundaries of the room's layout.
     *  - The method may return `null` if no valid path is found from the current position to the target position.
    */
    public void findPath() {
        boolean hasValidRoom = this.room != null;
        boolean hasValidLayout = hasValidRoom && this.room.getLayout() != null;
        boolean hasValidTargetPosition = this.targetPosition != null;
        boolean isTargetPositionWalkable = hasValidTargetPosition && this.targetPosition.isWalkable();
        boolean canSitOrLayAtTarget = hasValidTargetPosition && hasValidRoom && this.room.canSitOrLayAt(this.targetPosition);
        boolean canOverrideTile = hasValidTargetPosition && this.canOverrideTile(this.targetPosition);

        if (hasValidLayout && (isTargetPositionWalkable || canSitOrLayAtTarget || canOverrideTile)) {
            Deque<RoomTile> newPath = this.room.getLayout().findPath(this.currentPosition, this.targetPosition, this.targetPosition, this);
            if (newPath != null) {
                this.setPath(newPath);
            }
        }
    }

    public boolean isWalking() {
        return !this.isAtGoal() && this.canWalk;
    }

    public boolean hasStatus(RoomUnitStatus key) {
        return this.statuses.containsKey(key);
    }

    public String getStatus(RoomUnitStatus key) {
        return this.statuses.get(key);
    }

    public String getCurrentStatuses() {
        StringBuilder status = new StringBuilder("/");

        for (Map.Entry<RoomUnitStatus, String> entry : this.statuses.entrySet()) {
            status.append(entry.getKey()).append(" ").append(entry.getValue()).append("/");
        }

        return status.toString();
    }

    public void addStatus(RoomUnitStatus key, String value) {
        if (key != null && value != null) {
            this.statuses.put(key, value);
            this.statusUpdateNeeded = true;
        }
    }

    public RoomUnit removeStatus(RoomUnitStatus key) {
        String statusRemoved = this.statuses.remove(key);

        if(statusRemoved != null) {
            this.statusUpdateNeeded = true;
        }

        return this;
    }

    public void clearStatuses() {
        this.statuses.clear();
    }

    public void setRightsLevel(RoomRightLevels rightsLevel) {
        this.rightsLevel = rightsLevel;
        this.statusUpdateNeeded = true;
    }

    public void makeStand() {
        RoomItem item = this.room.getRoomItemManager().getTopItemAt(this.currentPosition.getX(), this.currentPosition.getY());
        if (item == null || !item.getBaseItem().allowSit() || !item.getBaseItem().allowLay()) {
            this.cmdStandEnabled = true;
            this.bodyRotation = RoomRotation.values()[this.getBodyRotation().getValue() - this.getBodyRotation().getValue() % 2];
            this.removeStatus(RoomUnitStatus.SIT);
            this.instantUpdate();
        }
    }

    public void makeSit() {
        if (this.hasStatus(RoomUnitStatus.SIT) || !this.canForcePosture()) {
            return;
        }

        this.cmdSitEnabled = true;
        this.bodyRotation = RoomRotation.values()[this.getBodyRotation().getValue() - this.getBodyRotation().getValue() % 2];
        this.addStatus(RoomUnitStatus.SIT, "0.5");

        if(this instanceof RoomAvatar roomAvatar) {
            roomAvatar.setDance(DanceType.NONE);
        }

        this.instantUpdate();
    }

    public void increaseTimeInRoom() {
        this.timeInRoom++;
    }

    public void resetTimeInRoom() {
        this.timeInRoom = 0;
    }

    public void lookAtPoint(RoomTile location) {
        if (!this.isCanRotate()) {
            return;
        }

        if (Emulator.getPluginManager().isRegistered(RoomUnitLookAtPointEvent.class, false)) {
            Event lookAtPointEvent = new RoomUnitLookAtPointEvent(this.room, this, location);
            Emulator.getPluginManager().fireEvent(lookAtPointEvent);

            if (lookAtPointEvent.isCancelled())
                return;
        }

        if (this.statuses.containsKey(RoomUnitStatus.LAY)) {
            return;
        }

        RoomRotation rotation = (RoomRotation.values()[Rotation.Calculate(this.currentPosition.getX(), this.currentPosition.getY(), location.getX(), location.getY())]);

        if (!this.statuses.containsKey(RoomUnitStatus.SIT)) {
            this.bodyRotation = rotation;
        }

        if (Math.abs(rotation.getValue() - this.bodyRotation.getValue()) <= 1) {
            this.headRotation = rotation;
        }

        this.statusUpdateNeeded = true;
    }

    public boolean canOverrideTile(RoomTile tile) {
        if (tile == null || this.room == null || this.room.getLayout() == null) return false;

        if (this.room.getRoomItemManager().getItemsAt(tile).stream().anyMatch(i -> i.canOverrideTile(this, this.room, tile)))
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
        if (this.room == null || this.room.getLayout() == null) return;

        int tileIndex = (tile.getX() & 0xFF) | (tile.getY() << 12);
        this.overridableTiles.remove(tileIndex);
    }

    public boolean canForcePosture() {
        if (this.room == null) return false;

        RoomItem topItem = this.room.getRoomItemManager().getTopItemAt(this.currentPosition.getX(), this.currentPosition.getY());

        return (!(topItem instanceof InteractionWater) && !(topItem instanceof InteractionWaterItem));
    }

    public RoomTile getClosestTile(List<RoomTile> tiles) {
        return tiles.stream().min(Comparator.comparingDouble(a -> a.distance(this.currentPosition))).orElse(null);
    }

    public RoomTile getClosestAdjacentTile(short x, short y, boolean diagonal) {
        if (this.room == null) return null;

        RoomTile baseTile = this.room.getLayout().getTile(x, y);

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
                        .map(rotation -> this.room.getLayout().getTileInFront(baseTile, rotation))
                        .filter(t -> t != null && t.isWalkable() && (this.currentPosition.equals(t) || !this.room.getRoomUnitManager().hasHabbosAt(t)))
                        .toList()
        );
    }

    public void handleSitStatus() {
        if(this.getCurrentItem() == null || !this.getCurrentItem().getBaseItem().allowSit()) {
            return;
        }

        if(!this.isCmdSitEnabled()) {
            if(this.currentPosition.getState().equals(RoomTileState.SIT) && !this.hasStatus(RoomUnitStatus.SIT)) {
                this.addStatus(RoomUnitStatus.SIT, String.valueOf(Item.getCurrentHeight(this.getCurrentItem())));
                this.setCurrentZ(this.getCurrentItem().getCurrentZ());
                this.setRotation(RoomRotation.values()[this.getCurrentItem().getRotation()]);
            } else if(!this.getCurrentItem().getBaseItem().allowSit() && this.hasStatus(RoomUnitStatus.SIT)) {
                this.removeStatus(RoomUnitStatus.SIT);
                this.instantUpdate();
            }
        }
    }

    public void handleLayStatus() {
        if(this.getCurrentItem() == null || !this.getCurrentItem().getBaseItem().allowLay()) {
            return;
        }

        if(!this.isCmdLayEnabled()) {
            if(this.currentPosition.getState().equals(RoomTileState.LAY) && !this.hasStatus(RoomUnitStatus.LAY)) {
                this.addStatus(RoomUnitStatus.LAY, String.valueOf(Item.getCurrentHeight(this.getCurrentItem())));
                this.setRotation(RoomRotation.values()[this.getCurrentItem().getRotation() % 4]);
            } else if (!this.getCurrentItem().getBaseItem().allowLay() && this.hasStatus(RoomUnitStatus.LAY)) {
                this.removeStatus(RoomUnitStatus.LAY);
                this.instantUpdate();
            }
        }
    }

    /**
     * Performs an instant update of the character's status to the connected room, if needed.
     * The method checks if a status update is required based on the internal flag `statusUpdateNeeded`.
     * If an update is needed, the method sends the character's updated status information to the room
     * using the {@link UserUpdateComposer}, effectively synchronizing the character's status with other room participants.
     * After the update is sent, the `statusUpdateNeeded` flag is reset to false until the next change in the character's status.
     * Note: This method is typically called when an immediate status update is necessary, such as when a status change occurs
     * and should be communicated to other room users without delay.
    */
    public void instantUpdate() {
        if(this.statusUpdateNeeded) {
            this.statusUpdateNeeded = false;
            this.room.sendComposer(new UserUpdateComposer(this).compose());
        }
    }

    /**
     * Processes the character's walking behavior based on its current walking state.
     * If the character is currently walking, the method executes the necessary steps to continue the movement.
     * The process involves updating the character's status, position, rotation, and height while moving along the computed path.
     * If the character reaches the destination tile, walking is stopped.
     * If the character encounters an invalid tile during its path, it recalculates the path to find an alternative route.
     * The method also handles fast walking when enabled, allowing the character to move more quickly through the path.
     * Note: This method is typically called in a loop to facilitate continuous character movement.
    */
    public void processWalking() {
        this.statuses.entrySet().removeIf(entry -> entry.getKey().isRemoveWhenWalking());

        if (this.getNextPosition() != null) {
            Collection<Habbo> habbosAtCurrentPosition = this.room.getRoomUnitManager().getHabbosAt(this.getCurrentPosition());
            for (Habbo h : habbosAtCurrentPosition) {
                try {
                    h.getRoomUnit().getCurrentItem().onWalkOff(h.getRoomUnit(), this.room, null);
                } catch (Exception ex) {
                    // Handle other exceptions appropriately
                }
            }
            this.setCurrentPosition(this.getNextPosition());
            this.setCurrentZ(this.getNextZ());
        }

        if (this.path != null && !this.path.isEmpty()) {
            RoomTile next = this.path.poll();

            if (this.path.size() > 1 && (this.cmdFastWalkEnabled || this.temporalFastWalkEnabled)) {
                next = this.path.poll();
            }

            if (next == null || !this.isValidTile(next)) {
                this.path.clear();
                this.findPath();

                if (this.path.isEmpty()) {
                    return;
                }

                next = this.path.poll();
            }

            RoomRotation nextRotation = this.handleNextRotation(next);
            double nextHeight = this.handleNextHeight(next);

            this.setRotation(nextRotation);
            this.addStatus(RoomUnitStatus.MOVE, next.getX() + "," + next.getY() + "," + nextHeight);
            this.nextPosition = next;
            this.nextZ = nextHeight;
        } else {
            this.stopWalking();
            Collection<Habbo> habbosAtCurrentPosition = this.room.getRoomUnitManager().getHabbosAt(this.getCurrentPosition());
            for (Habbo h : habbosAtCurrentPosition) {
                try {
                    h.getRoomUnit().getCurrentItem().onWalkOn(h.getRoomUnit(), this.room, null);
                } catch (Exception ex) {
                    // Handle other exceptions appropriately
                }
            }
        }
    }


    private RoomRotation handleNextRotation(RoomTile next) {
        return RoomRotation.values()[Rotation.Calculate(this.currentPosition.getX(), this.currentPosition.getY(), next.getX(), next.getY())];
    }

    private double handleNextHeight(RoomTile next) {
        double height = 0.0D;

        if(this instanceof RoomAvatar roomAvatar && roomAvatar.isRiding()) {
            height += 1.0D;
        }

        RoomItem nextTileItem = this.room.getRoomItemManager().getTopItemAt(next);

        if(nextTileItem != null) {
            height += nextTileItem.getNextZ();

            if (!nextTileItem.getBaseItem().allowSit() && !nextTileItem.getBaseItem().allowLay()) {
                height += Item.getCurrentHeight(nextTileItem);
            }
        } else {
            height += this.room.getLayout().getHeightAtSquare(next.getX(), next.getY());
        }

        return height;
    }

    /**
     * Checks whether the provided {@link RoomTile} is a valid tile for the character to walk on.
     *
     * @param tile The {@link RoomTile} to be validated.
     * @return {@code true} if the tile is valid for walking, {@code false} otherwise.
     *         Returns {@code true} if the character can override the tile (e.g., walk on furniture).
     *         Otherwise, the method checks various conditions to determine the tile's validity:
     *         - The tile's height difference from the character's current height should be within the allowable step height range.
     *         - The tile should not be blocked, invalid, or have an open state with a height difference above the maximum step height.
     *         - If the room allows walkthrough, the tile should not be occupied by other room units (excluding the character's target position).
     *         - If the room disallows walkthrough, the tile should not be occupied by any room units.
     *         - If there's a room item on the tile, it is considered a valid tile.
    */
    private boolean isValidTile(RoomTile tile) {
        boolean canOverrideTile = this.canOverrideTile(tile);

        if (canOverrideTile) {
            return true;
        }

        double heightDifference = tile.getStackHeight() - this.currentZ;

        RoomUnit exception = null;

        if(this instanceof RoomAvatar roomAvatar && roomAvatar.isRiding()) {
            exception = roomAvatar.getRidingPet().getRoomUnit();
        }

        boolean areRoomUnitsAtTile = this.room.getRoomUnitManager().areRoomUnitsAt(tile, exception);

        boolean isAboveMaximumStepHeight = (!RoomLayout.ALLOW_FALLING && heightDifference < -RoomLayout.MAXIMUM_STEP_HEIGHT);
        boolean isOpenTileAboveMaxHeight = (tile.getState() == RoomTileState.OPEN && heightDifference > RoomLayout.MAXIMUM_STEP_HEIGHT);
        boolean isTileBlocked = tile.getState().equals(RoomTileState.BLOCKED) || tile.getState().equals(RoomTileState.INVALID);

        if(isTileBlocked || isAboveMaximumStepHeight || isOpenTileAboveMaxHeight) {
            return false;
        } else {
            if(areRoomUnitsAtTile && this.targetPosition.equals(tile)) {
                this.stopWalking();
                return false;
            }

            if(areRoomUnitsAtTile && !this.room.getRoomInfo().isAllowWalkthrough()) {
                return false;
            }
        }

        RoomItem item = this.room.getRoomItemManager().getTopItemAt(tile);

        if(item != null) {
            return true;
        }

        return true;
    }

    public void clear() {
        super.clear();

        this.canWalk = true;
        this.canRotate = true;
        this.cmdFastWalkEnabled = false;
        this.clearStatuses();
        this.isKicked = false;
        this.cacheable.clear();
    }
}
