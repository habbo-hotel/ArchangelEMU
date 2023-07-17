package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveAvatarEvent extends MessageHandler {


    @Override
    public int getRatelimit() {
        return 500;
    }

    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getRoomUnit().getRoom() != null) {
            int x = this.packet.readInt(); // Position X
            int y = this.packet.readInt(); // Position Y

            // Get Habbo object
            Habbo habbo = this.client.getHabbo();

            if(habbo == null) {
                return;
            }

            // Get Room Habbo object (Unique GUID?)
            RoomUnit roomUnit = this.client.getHabbo().getRoomUnit();

            if(roomUnit == null) {
                return;
            }

            // If habbo is teleporting, don't calculate a new path
            if (roomUnit.isTeleporting()) {
                return;
            }

            // If habbo is being kicked don't calculate a new path
            if (roomUnit.isKicked()) {
                return;
            }

            // Get the room the habbo is in
            Room room = habbo.getRoomUnit().getRoom();

            if (room == null || room.getLayout() == null) {
                return;
            }

            // Don't calulcate a new path if are already at the end position
            if (x == roomUnit.getCurrentPosition().getX() && y == roomUnit.getCurrentPosition().getY()) {
                return;
            }

            // If habbo has control (im assuming admin, do something else, but we dont care about this part here)
            if (roomUnit.getCacheable().get("control") != null) {
                habbo = (Habbo) roomUnit.getCacheable().get("control");

                if (habbo.getRoomUnit().getRoom() != room) {
                    habbo.getRoomUnit().getCacheable().remove("controller");
                    this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                    habbo = this.client.getHabbo();
                }
            }

            // Recover roomUnit if necessary
            roomUnit = habbo.getRoomUnit();

            // If our room unit is not nullptr and we are in a room and we can walk, then calculate a new path
            if (roomUnit != null && roomUnit.isInRoom() && roomUnit.isCanWalk()) {

                //If teleport command is enabled
                if(roomUnit.isCmdTeleportEnabled()) {
                    RoomTile t = room.getLayout().getTile((short) x, (short) y);

                    if (habbo.getHabboInfo().getRiding() != null) {
                        room.sendComposer(new RoomUnitOnRollerComposer(roomUnit, null, roomUnit.getCurrentPosition(), roomUnit.getCurrentZ(), t, t.getStackHeight() + 1.0D, room).compose());
                        room.sendComposer(new RoomUnitOnRollerComposer(habbo.getHabboInfo().getRiding().getRoomUnit(), t, room).compose());
                    } else {
                        room.sendComposer(new RoomUnitOnRollerComposer(roomUnit, t, room).compose());
                    }

                    return;
                }

                // Don't calculate a new path if we are on a horse
                if (habbo.getHabboInfo().getRiding() != null && habbo.getHabboInfo().getRiding().getTask() != null && habbo.getHabboInfo().getRiding().getTask().equals(PetTasks.JUMP))
                    return;

                // Reset idle status
                if (roomUnit.isIdle()) {
                    UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                    Emulator.getPluginManager().fireEvent(event);

                    if (!event.isCancelled()) {
                        if (!event.isIdle()) {
                            if (roomUnit.getRoom() != null) roomUnit.getRoom().unIdle(habbo);
                            roomUnit.resetIdleTimer();
                        }
                    }
                }

                // Get room height map
                RoomTile tile = room.getLayout().getTile((short) x, (short) y);

                // this should never happen, if it does it would be a design flaw
                if (tile == null) {
                    return;
                }

                // Don't care
                if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.LAY)) {
                    if (room.getLayout().getTilesInFront(habbo.getRoomUnit().getCurrentPosition(), habbo.getRoomUnit().getBodyRotation().getValue(), 2).contains(tile))
                        return;
                }

                if (room.canLayAt(tile.getX(), tile.getY())) {
                    RoomItem bed = room.getTopItemAt(tile.getX(), tile.getY());

                    if (bed != null && bed.getBaseItem().allowLay()) {
                        room.getLayout().getTile(bed.getX(), bed.getY());
                        RoomTile pillow = switch (bed.getRotation()) {
                            case 0, 4 -> room.getLayout().getTile((short) x, bed.getY());
                            case 2, 8 -> room.getLayout().getTile(bed.getX(), (short) y);
                            default -> room.getLayout().getTile(bed.getX(), bed.getY());
                        };

                        if (pillow != null && room.canLayAt(pillow.getX(), pillow.getY())) {
                            roomUnit.setGoalLocation(pillow);
                            return;
                        }
                    }
                }

                THashSet<RoomItem> items = room.getItemsAt(tile);

                if (items.size() > 0) {
                    for (RoomItem item : items) {
                        RoomTile overriddenTile = item.getOverrideGoalTile(roomUnit, room, tile);

                        if (overriddenTile == null) {
                            return; // null cancels the entire event
                        }

                        if (!overriddenTile.equals(tile) && overriddenTile.isWalkable()) {
                            tile = overriddenTile;
                            break;
                        }
                    }
                }

                // This is where we set the end location and begin finding a path
                if (tile.isWalkable() || room.canSitOrLayAt(tile.getX(), tile.getY())) {
                    roomUnit.setGoalLocation(tile);
                }
            }
        }
    }
}
