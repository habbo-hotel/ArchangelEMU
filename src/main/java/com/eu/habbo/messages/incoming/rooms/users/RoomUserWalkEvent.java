package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import gnu.trove.set.hash.THashSet;

public class RoomUserWalkEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            int x = this.packet.readInt();
            int y = this.packet.readInt();

            Habbo habbo = this.client.getHabbo();
            RoomUnit roomUnit = this.client.getHabbo().getRoomUnit();

            if (roomUnit.isTeleporting)
                return;

            if (roomUnit.isKicked)
                return;

            if (roomUnit.getCacheable().get("control") != null) {
                habbo = (Habbo) roomUnit.getCacheable().get("control");

                if (habbo.getHabboInfo().getCurrentRoom() != this.client.getHabbo().getHabboInfo().getCurrentRoom()) {
                    habbo.getRoomUnit().getCacheable().remove("controller");
                    this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                    habbo = this.client.getHabbo();
                }
            }

            roomUnit = habbo.getRoomUnit();
            Room room = habbo.getHabboInfo().getCurrentRoom();

            try {
                if (roomUnit != null && roomUnit.isInRoom() && roomUnit.canWalk()) {
                    if (!roomUnit.cmdTeleport) {
                        if (habbo.getHabboInfo().getRiding() != null && habbo.getHabboInfo().getRiding().getTask() != null && habbo.getHabboInfo().getRiding().getTask().equals(PetTasks.JUMP))
                            return;

                        if (x == roomUnit.getX() && y == roomUnit.getY())
                            return;

                        if (room == null || room.getLayout() == null)
                            return;

                        if (roomUnit.isIdle()) {
                            UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                            Emulator.getPluginManager().fireEvent(event);

                            if (!event.isCancelled()) {
                                if (!event.idle) {
                                    roomUnit.getRoom().unIdle(habbo);
                                    roomUnit.resetIdleTimer();
                                }
                            }
                        }

                        RoomTile tile = room.getLayout().getTile((short) x, (short) y);

                        if (tile == null) {
                            return;
                        }

                        if (habbo.getRoomUnit().hasStatus(RoomUnitStatus.LAY)) {
                            if (room.getLayout().getTilesInFront(habbo.getRoomUnit().getCurrentLocation(), habbo.getRoomUnit().getBodyRotation().getValue(), 2).contains(tile))
                                return;
                        }
                        if (room.canLayAt(tile.x, tile.y)) {
                            HabboItem bed = room.getTopItemAt(tile.x, tile.y);

                            if (bed != null && bed.getBaseItem().allowLay()) {
                                RoomTile pillow = room.getLayout().getTile(bed.getX(), bed.getY());
                                switch (bed.getRotation()) {
                                    case 0:
                                    case 4:
                                        pillow = room.getLayout().getTile((short)x, bed.getY());
                                        break;
                                    case 2:
                                    case 8:
                                        pillow = room.getLayout().getTile(bed.getX(), (short)y);
                                        break;
                                }

                                if (pillow != null && room.canLayAt(pillow.x, pillow.y)) {
                                    roomUnit.setGoalLocation(pillow);
                                    return;
                                }
                            }
                        }
                        if (tile.isWalkable() || room.canSitOrLayAt(tile.x, tile.y)) {
                            roomUnit.setGoalLocation(tile);
                        }
                    } else {
                        RoomTile t = room.getLayout().getTile((short) x, (short) y);
                        room.sendComposer(new RoomUnitOnRollerComposer(roomUnit, t, room).compose());

                        if (habbo.getHabboInfo().getRiding() != null) {
                            room.sendComposer(new RoomUnitOnRollerComposer(habbo.getHabboInfo().getRiding().getRoomUnit(), t, room).compose());
                        }
                    }
                }
            } catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }
}
