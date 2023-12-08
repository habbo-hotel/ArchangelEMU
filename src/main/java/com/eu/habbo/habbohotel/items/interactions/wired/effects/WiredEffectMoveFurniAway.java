package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

public class WiredEffectMoveFurniAway extends InteractionWiredEffect {
    public WiredEffectMoveFurniAway(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniAway(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        for (RoomItem item : this.getWiredSettings().getItems(room)) {
            RoomTile t = room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());

            RoomUnit target = room.getRoomUnitManager().getCurrentRoomUnits().values().stream().min(Comparator.comparingDouble(a -> a.getCurrentPosition().distance(t))).orElse(null);

            if (target != null) {
                if (target.getCurrentPosition().distance(t) <= 1) {
                    Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.COLLISION, target, room, new Object[]{item}), 500);
                    continue;
                }

                int x = 0;
                int y = 0;

                if (target.getCurrentPosition().getX() == item.getCurrentPosition().getX()) {
                    if (item.getCurrentPosition().getY() < target.getCurrentPosition().getY())
                        y--;
                    else
                        y++;
                } else {
                    if (target.getCurrentPosition().getY() == item.getCurrentPosition().getY()) {
                        if (item.getCurrentPosition().getX() < target.getCurrentPosition().getX())
                            x--;
                        else
                            x++;
                    } else {
                        if (target.getCurrentPosition().getX() - item.getCurrentPosition().getX() > target.getCurrentPosition().getY() - item.getCurrentPosition().getY()) {
                            if (target.getCurrentPosition().getX() - item.getCurrentPosition().getX() > 0)
                                x--;
                            else
                                x++;
                        } else {
                            if (target.getCurrentPosition().getY() - item.getCurrentPosition().getY() > 0)
                                y--;
                            else
                                y++;
                        }
                    }
                }

                RoomTile newLocation = room.getLayout().getTile((short) (item.getCurrentPosition().getX() + x), (short) (item.getCurrentPosition().getY() + y));
                RoomTile oldLocation = room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY());
                double oldZ = item.getCurrentZ();

                if (newLocation != null && newLocation.getState() != RoomTileState.INVALID && newLocation != oldLocation && room.getRoomItemManager().furnitureFitsAt(newLocation, item, item.getRotation(), true) == FurnitureMovementError.NONE) {
                    if (room.getRoomItemManager().moveItemTo(item, newLocation, item.getRotation(), null, false, true) == FurnitureMovementError.NONE) {
                        room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newLocation, item.getCurrentZ(), 0, room).compose());
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
        return WiredEffectType.FLEE;
    }
}
