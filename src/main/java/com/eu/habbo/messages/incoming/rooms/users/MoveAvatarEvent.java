package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
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
            RoomHabbo roomHabbo = this.client.getHabbo().getRoomUnit();

            if(roomHabbo == null) {
                return;
            }

            // If habbo is teleporting, don't calculate a new path
            if (roomHabbo.isTeleporting()) {
                return;
            }

            // If habbo is being kicked don't calculate a new path
            if (roomHabbo.isKicked()) {
                return;
            }

            //Is going to ride a pet, can't cancel
            if(roomHabbo.isRideLock()) {
                return;
            }

            // Get the room the habbo is in
            Room room = habbo.getRoomUnit().getRoom();

            if (room == null || room.getLayout() == null) {
                return;
            }

            // Don't calulcate a new path if are already at the end position
            if (x == roomHabbo.getCurrentPosition().getX() && y == roomHabbo.getCurrentPosition().getY()) {
                return;
            }

            // If habbo has control (im assuming admin, do something else, but we dont care about this part here)
            if (roomHabbo.getCacheable().get("control") != null) {
                habbo = (Habbo) roomHabbo.getCacheable().get("control");

                if (habbo.getRoomUnit().getRoom() != room) {
                    habbo.getRoomUnit().getCacheable().remove("controller");
                    this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                    habbo = this.client.getHabbo();
                }
            }

            // Recover roomUnit if necessary
            roomHabbo = habbo.getRoomUnit();

            // If our room unit is not nullptr and we are in a room and we can walk, then calculate a new path
            if (roomHabbo != null && roomHabbo.isInRoom() && roomHabbo.isCanWalk()) {

                //If teleport command is enabled
                if(roomHabbo.isCmdTeleportEnabled()) {
                    RoomTile t = room.getLayout().getTile((short) x, (short) y);

                    if (habbo.getHabboInfo().getRiding() != null) {
                        room.sendComposer(new RoomUnitOnRollerComposer(roomHabbo, null, roomHabbo.getCurrentPosition(), roomHabbo.getCurrentZ(), t, t.getStackHeight() + 1.0D, room).compose());
                        room.sendComposer(new RoomUnitOnRollerComposer(habbo.getHabboInfo().getRiding().getRoomUnit(), t, room).compose());
                    } else {
                        room.sendComposer(new RoomUnitOnRollerComposer(roomHabbo, t, room).compose());
                    }

                    return;
                }

                // Don't calculate a new path if we are on a horse
                if (habbo.getHabboInfo().getRiding() != null && habbo.getHabboInfo().getRiding().getTask() != null && habbo.getHabboInfo().getRiding().getTask().equals(PetTasks.JUMP)) {
                    return;
                }

                // Reset idle status
                if (roomHabbo.isIdle()) {
                    UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                    Emulator.getPluginManager().fireEvent(event);

                    if (!event.isCancelled()) {
                        if (!event.isIdle()) {
                            if (roomHabbo.getRoom() != null) {
                                roomHabbo.unIdle();
                            }
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

                if (room.canLayAt(tile)) {
                    RoomItem bed = room.getRoomItemManager().getTopItemAt(tile.getX(), tile.getY());

                    if (bed != null && bed.getBaseItem().allowLay()) {
                        room.getLayout().getTile(bed.getCurrentPosition().getX(), bed.getCurrentPosition().getY());
                        RoomTile pillow = switch (bed.getRotation()) {
                            case 0, 4 -> room.getLayout().getTile((short) x, bed.getCurrentPosition().getY());
                            case 2, 8 -> room.getLayout().getTile(bed.getCurrentPosition().getX(), (short) y);
                            default ->
                                    room.getLayout().getTile(bed.getCurrentPosition().getX(), bed.getCurrentPosition().getY());
                        };

                        if (pillow != null && room.canLayAt(pillow)) {
                            roomHabbo.setGoalLocation(pillow);
                            return;
                        }
                    }
                }

                THashSet<RoomItem> items = room.getRoomItemManager().getItemsAt(tile);

                if (items.size() > 0) {
                    for (RoomItem item : items) {
                        RoomTile overriddenTile = item.getOverrideGoalTile(roomHabbo, room, tile);

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
                    roomHabbo.setGoalLocation(tile);
                }
            }
        }
    }
}
