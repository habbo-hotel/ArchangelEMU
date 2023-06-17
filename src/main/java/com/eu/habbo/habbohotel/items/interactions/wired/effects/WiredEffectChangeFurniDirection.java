package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredChangeDirectionSetting;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class WiredEffectChangeFurniDirection extends InteractionWiredEffect {
    public final int PARAM_START_ROTATION = 0;
    public final int PARAM_BLOCKED_ACTION = 1;
    public static final int ACTION_WAIT = 0;
    public static final int ACTION_TURN_RIGHT_45 = 1;
    public static final int ACTION_TURN_RIGHT_90 = 2;
    public static final int ACTION_TURN_LEFT_45 = 3;
    public static final int ACTION_TURN_LEFT_90 = 4;
    public static final int ACTION_TURN_BACK = 5;
    public static final int ACTION_TURN_RANDOM = 6;

    public WiredEffectChangeFurniDirection(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectChangeFurniDirection(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        int startRotationValue = this.getWiredSettings().getIntegerParams().get(PARAM_START_ROTATION);
        int blockActionValue = this.getWiredSettings().getIntegerParams().get(PARAM_BLOCKED_ACTION);

        if(startRotationValue < 0 || startRotationValue > 7 || (startRotationValue % 2) != 0) {
            return false;
        }

        if(blockActionValue < 0 || blockActionValue > 6) {
            return false;
        }

        RoomUserRotation startRotation = RoomUserRotation.fromValue(startRotationValue);

        for(HabboItem item : this.getWiredSettings().getItems(room)) {
            WiredChangeDirectionSetting setting = new WiredChangeDirectionSetting(item.getId(), item.getRotation(), startRotation);
            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), setting.getDirection().getValue());
            int count = 1;
            while ((targetTile == null || targetTile.getState() == RoomTileState.INVALID || room.furnitureFitsAt(targetTile, item, item.getRotation(), false) != FurnitureMovementError.NONE) && count < 8) {
                setting.setDirection(this.nextRotation(setting.getDirection()));

                RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), setting.getDirection().getValue());
                if (tile != null && tile.getState() != RoomTileState.INVALID) {
                    targetTile = tile;
                }

                count++;
            }

            int newDirectionValue = setting.getDirection().getValue();

            RoomTile newTargetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), newDirectionValue);

            if(item.getRotation() != setting.getRotation()) {
                if(room.furnitureFitsAt(newTargetTile, item, setting.getRotation(), false) != FurnitureMovementError.NONE)
                    continue;

                room.moveFurniTo(item, newTargetTile, setting.getRotation(), null, true);
            }

            boolean hasRoomUnits = false;
            THashSet<RoomTile> newOccupiedTiles = room.getLayout().getTilesAt(newTargetTile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            for(RoomTile tile : newOccupiedTiles) {
                for (RoomUnit _roomUnit : room.getRoomUnits(tile)) {
                    hasRoomUnits = true;
                    if(_roomUnit.getCurrentLocation() == newTargetTile) {
                        Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.COLLISION, _roomUnit, room, new Object[]{item}));
                        break;
                    }
                }
            }

            if (newTargetTile != null && newTargetTile.getState() != RoomTileState.INVALID && room.furnitureFitsAt(targetTile, item, item.getRotation(), false) == FurnitureMovementError.NONE) {
                if (!hasRoomUnits) {
                    RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
                    double oldZ = item.getZ();
                    if(room.moveFurniTo(item, newTargetTile, item.getRotation(), null, false) == FurnitureMovementError.NONE) {
                        room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, targetTile, item.getZ(), 0, room).compose());
                    }
                }
            }
        }

        return false;
    }

    private RoomUserRotation nextRotation(RoomUserRotation currentRotation) {
        return switch (this.getWiredSettings().getIntegerParams().get(PARAM_BLOCKED_ACTION)) {
            case ACTION_TURN_BACK -> RoomUserRotation.fromValue(currentRotation.getValue()).getOpposite();
            case ACTION_TURN_LEFT_45 -> RoomUserRotation.counterClockwise(currentRotation);
            case ACTION_TURN_LEFT_90 -> RoomUserRotation.counterClockwise(RoomUserRotation.counterClockwise(currentRotation));
            case ACTION_TURN_RIGHT_45 -> RoomUserRotation.clockwise(currentRotation);
            case ACTION_TURN_RIGHT_90 -> RoomUserRotation.clockwise(RoomUserRotation.clockwise(currentRotation));
            case ACTION_TURN_RANDOM -> RoomUserRotation.fromValue(Emulator.getRandom().nextInt(8));
            case ACTION_WAIT -> currentRotation;
            default -> currentRotation;
        };
    }

    @Override
    protected long requiredCooldown() {
        return 495;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.MOVE_DIRECTION;
    }
}
