package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
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

    public WiredEffectMoveFurniAway(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        for (HabboItem item : this.getWiredSettings().getItems(room)) {
            RoomTile t = room.getLayout().getTile(item.getX(), item.getY());

            RoomUnit target = room.getRoomUnits().stream().min(Comparator.comparingDouble(a -> a.getCurrentLocation().distance(t))).orElse(null);

            if (target != null) {
                if (target.getCurrentLocation().distance(t) <= 1) {
                    Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.COLLISION, target, room, new Object[]{item}), 500);
                    continue;
                }

                int x = 0;
                int y = 0;

                if (target.getX() == item.getX()) {
                    if (item.getY() < target.getY())
                        y--;
                    else
                        y++;
                } else if (target.getY() == item.getY()) {
                    if (item.getX() < target.getX())
                        x--;
                    else
                        x++;
                } else if (target.getX() - item.getX() > target.getY() - item.getY()) {
                    if (target.getX() - item.getX() > 0)
                        x--;
                    else
                        x++;
                } else {
                    if (target.getY() - item.getY() > 0)
                        y--;
                    else
                        y++;
                }

                RoomTile newLocation = room.getLayout().getTile((short) (item.getX() + x), (short) (item.getY() + y));
                RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
                double oldZ = item.getZ();

                if(newLocation != null && newLocation.getState() != RoomTileState.INVALID && newLocation != oldLocation && room.furnitureFitsAt(newLocation, item, item.getRotation(), true) == FurnitureMovementError.NONE) {
                    if(room.moveFurniTo(item, newLocation, item.getRotation(), null, false) == FurnitureMovementError.NONE) {
                        room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newLocation, item.getZ(), 0, room).compose());
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
