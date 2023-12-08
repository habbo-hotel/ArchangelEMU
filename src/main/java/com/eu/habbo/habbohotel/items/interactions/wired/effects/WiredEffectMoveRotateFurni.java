package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.constants.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.constants.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

@Slf4j
public class WiredEffectMoveRotateFurni extends InteractionWiredEffect implements ICycleable {
    public final int PARAM_DIRECTION = 0;
    public final int PARAM_ROTATION = 1;

    public WiredEffectMoveRotateFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveRotateFurni(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().size() == 0) {
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        int direction = this.getWiredSettings().getIntegerParams().get(PARAM_DIRECTION);
        int rotation = this.getWiredSettings().getIntegerParams().get(PARAM_ROTATION);

        for (RoomItem item : this.getWiredSettings().getItems(room)) {
            int newRotation = rotation > 0 ? this.getNewRotation(item, rotation) : item.getRotation();
            RoomTile newLocation = room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());
            RoomTile oldLocation = room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());
            double oldZ = item.getCurrentZ();

            if(direction > 0) {
                RoomRotation moveDirection = this.getMovementDirection(direction);
                newLocation = room.getLayout().getTile(
                    (short) (item.getCurrentPosition().getX() + ((moveDirection == RoomRotation.WEST || moveDirection == RoomRotation.NORTH_WEST || moveDirection == RoomRotation.SOUTH_WEST) ? -1 : (((moveDirection == RoomRotation.EAST || moveDirection == RoomRotation.SOUTH_EAST || moveDirection == RoomRotation.NORTH_EAST) ? 1 : 0)))),
                    (short) (item.getCurrentPosition().getY() + ((moveDirection == RoomRotation.NORTH || moveDirection == RoomRotation.NORTH_EAST || moveDirection == RoomRotation.NORTH_WEST) ? 1 : ((moveDirection == RoomRotation.SOUTH || moveDirection == RoomRotation.SOUTH_EAST || moveDirection == RoomRotation.SOUTH_WEST) ? -1 : 0)))
                );
            }

            boolean slideAnimation = item.getRotation() == newRotation;

            FurnitureMovementError furniMoveTest = room.getRoomItemManager().furnitureFitsAt(newLocation, item, newRotation, true);
            if(newLocation != null && newLocation.getState() != RoomTileState.INVALID && (newLocation != oldLocation || newRotation != item.getRotation()) && (furniMoveTest == FurnitureMovementError.NONE || ((furniMoveTest == FurnitureMovementError.TILE_HAS_BOTS || furniMoveTest == FurnitureMovementError.TILE_HAS_HABBOS || furniMoveTest == FurnitureMovementError.TILE_HAS_PETS) && newLocation == oldLocation))) {
                if(room.getRoomItemManager().furnitureFitsAt(newLocation, item, newRotation, false) == FurnitureMovementError.NONE) {
                    boolean sendUpdates = !slideAnimation;
                    if (room.getRoomItemManager().moveItemTo(item, newLocation, newRotation, null, sendUpdates, true) == FurnitureMovementError.NONE) {
                        if (slideAnimation) {
                            room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newLocation, item.getCurrentZ(), 0, room).compose());
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns a new rotation for an item based on the wired options
     *
     * @param item HabboItem
     * @return new rotation
     */
    private int getNewRotation(RoomItem item, int rotation) {

        if(item.getMaximumRotations() == 2) {
            return item.getRotation() == 0 ? 4 : 0;
        }
        else if(item.getMaximumRotations() == 1) {
            return item.getRotation();
        }
        else if(item.getMaximumRotations() > 4) {
            if (rotation == 1) {
                return item.getRotation() == item.getMaximumRotations() - 1 ? 0 : item.getRotation() + 1;
            } else if (rotation == 2) {
                return item.getRotation() > 0 ? item.getRotation() - 1 : item.getMaximumRotations() - 1;
            } else if (rotation == 3) { //Random rotation
                THashSet<Integer> possibleRotations = new THashSet<>();
                for (int i = 0; i < item.getMaximumRotations(); i++)
                {
                    possibleRotations.add(i);
                }

                possibleRotations.remove(item.getRotation());

                if(possibleRotations.size() > 0) {
                    int index = Emulator.getRandom().nextInt(possibleRotations.size());
                    Iterator<Integer> iter = possibleRotations.iterator();
                    for (int i = 0; i < index; i++) {
                        iter.next();
                    }
                    return iter.next();
                }
            }
        }
        else {
            if (rotation == 1) {
                return (item.getRotation() + 2) % 8;
            } else if (rotation == 2) {
                int rot = (item.getRotation() - 2) % 8;
                if(rot < 0) {
                    rot += 8;
                }
                return rot;
            } else if (rotation == 3) { //Random rotation
                THashSet<Integer> possibleRotations = new THashSet<>();
                for (int i = 0; i < item.getMaximumRotations(); i++)
                {
                    possibleRotations.add(i * 2);
                }

                possibleRotations.remove(item.getRotation());

                if(possibleRotations.size() > 0) {
                    int index = Emulator.getRandom().nextInt(possibleRotations.size());
                    Iterator<Integer> iter = possibleRotations.iterator();
                    for (int i = 0; i < index; i++) {
                        iter.next();
                    }
                    return iter.next();
                }
            }
        }

        return item.getRotation();
    }

    /**
     * Returns the direction of movement based on the wired settings
     *
     * @return direction
     */
    private RoomRotation getMovementDirection(int direction) {
        RoomRotation movemementDirection = RoomRotation.NORTH;
        if (direction == 1) {
            movemementDirection = RoomRotation.values()[Emulator.getRandom().nextInt(RoomRotation.values().length / 2) * 2];
        } else if (direction == 2) {
            if (Emulator.getRandom().nextInt(2) == 1) {
                movemementDirection = RoomRotation.EAST;
            } else {
                movemementDirection = RoomRotation.WEST;
            }
        } else if (direction == 3) {
            if (Emulator.getRandom().nextInt(2) != 1) {
                movemementDirection = RoomRotation.SOUTH;
            }
        } else if (direction == 4) {
            movemementDirection = RoomRotation.SOUTH;
        } else if (direction == 5) {
            movemementDirection = RoomRotation.EAST;
        } else if (direction == 7) {
            movemementDirection = RoomRotation.WEST;
        }
        return movemementDirection;
    }

    @Override
    public void cycle(Room room) {}

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.MOVE_ROTATE;
    }
}