package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.threading.runnables.WiredCollissionRunnable;
import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wired effect: move to closest user
 * Confirmed as working exactly like Habbo.com 03/05/2019 04:00
 *
 * @author Beny.
 */
public class WiredEffectMoveFurniTowards extends InteractionWiredEffect {
    private THashMap<Integer, RoomRotation> lastDirections;
    
    public WiredEffectMoveFurniTowards(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.lastDirections = new THashMap<>();
    }

    public WiredEffectMoveFurniTowards(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.lastDirections = new THashMap<>();
    }

    public List<RoomRotation> getAvailableDirections(RoomItem item, Room room) {
        List<RoomRotation> availableDirections = new ArrayList<>();
        RoomLayout layout = room.getLayout();

        RoomTile currentTile = layout.getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());

        RoomRotation[] rotations = new RoomRotation[]{RoomRotation.NORTH, RoomRotation.EAST, RoomRotation.SOUTH, RoomRotation.WEST};

        for (RoomRotation rot : rotations) {
            RoomTile tile = layout.getTileInFront(currentTile, rot.getValue());

            if (tile == null || tile.getState() == RoomTileState.BLOCKED || tile.getState() == RoomTileState.INVALID)
                continue;

            if (!layout.tileExists(tile.getX(), tile.getY()))
                continue;

            if (room.getRoomItemManager().furnitureFitsAt(tile, item, item.getRotation(), true) == FurnitureMovementError.INVALID_MOVE)
                continue;

            RoomItem topItem = room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY());
            if (topItem != null && !topItem.getBaseItem().allowStack())
                continue;

            if (tile.getAllowStack()) {
                availableDirections.add(rot);
            }
        }

        return availableDirections;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        for (RoomItem item : this.getWiredSettings().getItems(room)) {
            // direction the furni will move in
            RoomRotation moveDirection = null;
            RoomRotation lastDirection = lastDirections.get(item.getId());

            // 1. Check if any user is within 3 tiles from the item
            RoomUnit target = null; // closest found user
            RoomLayout layout = room.getLayout();
            boolean collided = false;

            if (layout == null) {
                break;
            }

            for (int i = 0; i < 3; i++) {
                if (target != null)
                    break;

                RoomRotation[] rotations = new RoomRotation[]{RoomRotation.NORTH, RoomRotation.EAST, RoomRotation.SOUTH, RoomRotation.WEST};

                for (RoomRotation rot : rotations) {
                    RoomTile startTile = layout.getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());

                    for (int ii = 0; ii <= i; ii++) {
                        if (startTile == null)
                            break;

                        startTile = layout.getTileInFront(startTile, rot.getValue());
                    }

                    if (startTile != null && layout.tileExists(startTile.getX(), startTile.getY())) {
                        Collection<RoomUnit> roomUnitsAtTile = room.getRoomUnitManager().getRoomUnitsAt(startTile);
                        if (roomUnitsAtTile.size() > 0) {
                            target = roomUnitsAtTile.iterator().next();
                            if (i == 0) { // i = 0 means right next to it
                                collided = true;
                                Emulator.getThreading().run(new WiredCollissionRunnable(target, room, new Object[]{item}));
                            }
                            break;
                        }
                    }
                }
            }

            if (collided)
                continue;

            if (target != null) {
                if (target.getCurrentPosition().getX() == item.getCurrentPosition().getX()) {
                    if (item.getCurrentPosition().getY() < target.getCurrentPosition().getY())
                        moveDirection = RoomRotation.SOUTH;
                    else
                        moveDirection = RoomRotation.NORTH;
                } else {
                    if (target.getCurrentPosition().getY() == item.getCurrentPosition().getY()) {
                        if (item.getCurrentPosition().getX() < target.getCurrentPosition().getX())
                            moveDirection = RoomRotation.EAST;
                        else
                            moveDirection = RoomRotation.WEST;
                    } else {
                        if (target.getCurrentPosition().getX() - item.getCurrentPosition().getX() > target.getCurrentPosition().getY() - item.getCurrentPosition().getY()) {
                            if (target.getCurrentPosition().getX() - item.getCurrentPosition().getX() > 0)
                                moveDirection = RoomRotation.EAST;
                            else
                                moveDirection = RoomRotation.WEST;
                        } else {
                            if (target.getCurrentPosition().getY() - item.getCurrentPosition().getY() > 0)
                                moveDirection = RoomRotation.SOUTH;
                            else
                                moveDirection = RoomRotation.NORTH;
                        }
                    }
                }
            }


            // 2. Get a random direction
            /*
            getAvailableDirections:
                0 available - don't move
                1 available - move in that direction
                2 available - if lastdirection = null move in random possible direction
                              else if direction[0] = lastdirection opposite, move in direction[1]
                              else move in direction[0]
                3+ available - move in random direction, but never the opposite
             */

            List<RoomRotation> availableDirections = this.getAvailableDirections(item, room);

            if (moveDirection != null && !availableDirections.contains(moveDirection))
                moveDirection = null;

            if (moveDirection == null) {
                if (availableDirections.size() == 0) {
                    continue;
                } else if (availableDirections.size() == 1) {
                    moveDirection = availableDirections.iterator().next();
                } else if (availableDirections.size() == 2) {
                    if (lastDirection == null) {
                        moveDirection = availableDirections.get(Emulator.getRandom().nextInt(availableDirections.size()));
                    } else {
                        RoomRotation oppositeLast = lastDirection.getOpposite();

                        if (availableDirections.get(0) == oppositeLast) {
                            moveDirection = availableDirections.get(1);
                        } else {
                            moveDirection = availableDirections.get(0);
                        }
                    }
                } else {
                    if (lastDirection != null) {
                        RoomRotation opposite = lastDirection.getOpposite();
                        availableDirections.remove(opposite);
                    }
                    moveDirection = availableDirections.get(Emulator.getRandom().nextInt(availableDirections.size()));
                }
            }

            RoomTile newTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()), moveDirection.getValue());

            RoomTile oldLocation = room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());
            double oldZ = item.getCurrentZ();

            if(newTile != null) {
                lastDirections.put(item.getId(), moveDirection);
                if (newTile.getState() != RoomTileState.INVALID && newTile != oldLocation && room.getRoomItemManager().furnitureFitsAt(newTile, item, item.getRotation(), true) == FurnitureMovementError.NONE) {
                    if (room.getRoomItemManager().moveItemTo(item, newTile, item.getRotation(), null, false, true) == FurnitureMovementError.NONE) {
                        room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newTile, item.getCurrentZ(), 0, room).compose());
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected long requiredCooldown() {
        return 495;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.CHASE;
    }
}
