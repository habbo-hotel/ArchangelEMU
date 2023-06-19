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
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class WiredEffectChangeFurniDirection extends InteractionWiredEffect {
    public final int PARAM_START_DIRECTION = 0;
    public final int PARAM_BLOCKED_ACTION = 1;
    public static final int ACTION_WAIT = 0;
    public static final int ACTION_TURN_RIGHT_45 = 1;
    public static final int ACTION_TURN_RIGHT_90 = 2;
    public static final int ACTION_TURN_LEFT_45 = 3;
    public static final int ACTION_TURN_LEFT_90 = 4;
    public static final int ACTION_TURN_BACK = 5;
    public static final int ACTION_TURN_RANDOM = 6;

    private int defaultDirectionValue;

    private int defaultBlockActionValue;

    private boolean requiresUpdate = false;
    private final HashMap<HabboItem, WiredChangeDirectionSetting> itemsSettings;

    public WiredEffectChangeFurniDirection(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.itemsSettings = new HashMap<>();
    }

    public WiredEffectChangeFurniDirection(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.itemsSettings = new HashMap<>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        int startDirectionValue = this.getWiredSettings().getIntegerParams().get(PARAM_START_DIRECTION);
        int blockActionValue = this.getWiredSettings().getIntegerParams().get(PARAM_BLOCKED_ACTION);

        if(startDirectionValue < 0 || startDirectionValue > 7 || (startDirectionValue % 2) != 0) {
            return false;
        }

        if(blockActionValue < 0 || blockActionValue > 6) {
            return false;
        }

        if(this.defaultDirectionValue != startDirectionValue) {
            this.defaultDirectionValue = startDirectionValue;
            this.requiresUpdate = true;
        }

        RoomUserRotation startDirection = RoomUserRotation.fromValue(startDirectionValue);

        if(this.requiresUpdate) {
            for (WiredChangeDirectionSetting setting : this.itemsSettings.values()) {
                setting.setDirection(startDirection);
            }
            this.requiresUpdate = false;
        }

        for(HabboItem item : this.getWiredSettings().getItems(room)) {
            WiredChangeDirectionSetting setting = null;
            if(!this.itemsSettings.containsKey(item)) {
                this.itemsSettings.put(item, new WiredChangeDirectionSetting(item.getId(), item.getRotation(), startDirection));
            } else {
                setting = this.itemsSettings.get(item);
            }

            if(setting == null) {
                continue;
            }

            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), setting.getDirection().getValue());
            int count = 1;
            while ((targetTile == null || targetTile.getState() == RoomTileState.INVALID || !room.tileWalkable(targetTile) || room.furnitureFitsAt(targetTile, item, item.getRotation(), false) != FurnitureMovementError.NONE) && count < 8) {
                setting.setDirection(this.nextDirection(setting.getDirection()));

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

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    private RoomUserRotation nextDirection(RoomUserRotation currentDirection) {
        return switch (this.getWiredSettings().getIntegerParams().get(PARAM_BLOCKED_ACTION)) {
            case ACTION_TURN_BACK -> RoomUserRotation.fromValue(currentDirection.getValue()).getOpposite();
            case ACTION_TURN_LEFT_45 -> RoomUserRotation.counterClockwise(currentDirection);
            case ACTION_TURN_LEFT_90 -> RoomUserRotation.counterClockwise(RoomUserRotation.counterClockwise(currentDirection));
            case ACTION_TURN_RIGHT_45 -> RoomUserRotation.clockwise(currentDirection);
            case ACTION_TURN_RIGHT_90 -> RoomUserRotation.clockwise(RoomUserRotation.clockwise(currentDirection));
            case ACTION_TURN_RANDOM -> RoomUserRotation.fromValue(Emulator.getRandom().nextInt(8));
            case ACTION_WAIT -> currentDirection;
            default -> currentDirection;
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
