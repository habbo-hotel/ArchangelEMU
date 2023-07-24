package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
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
    private final HashMap<RoomItem, WiredChangeDirectionSetting> itemsSettings;

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

        RoomRotation startDirection = RoomRotation.fromValue(startDirectionValue);

        if(this.requiresUpdate) {
            for (WiredChangeDirectionSetting setting : this.itemsSettings.values()) {
                setting.setDirection(startDirection);
            }
            this.requiresUpdate = false;
        }

        for(RoomItem item : this.getWiredSettings().getItems(room)) {
            WiredChangeDirectionSetting setting = this.itemsSettings.computeIfAbsent(item, k ->
                    new WiredChangeDirectionSetting(item.getId(), item.getRotation(), startDirection)
            );

            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), setting.getDirection().getValue());
            int count = 1;
            while ((targetTile == null || targetTile.getState() == RoomTileState.INVALID || !room.getLayout().tileWalkable(targetTile) || room.getRoomItemManager().furnitureFitsAt(targetTile, item, item.getRotation(), false) != FurnitureMovementError.NONE) && count < 8) {
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
                if(room.getRoomItemManager().furnitureFitsAt(newTargetTile, item, setting.getRotation(), false) != FurnitureMovementError.NONE)
                    continue;

                room.getRoomItemManager().moveItemTo(item, newTargetTile, setting.getRotation(), null, true, true);
            }

            boolean hasRoomUnits = false;

            THashSet<RoomTile> newOccupiedTiles = room.getLayout().getTilesAt(newTargetTile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            for(RoomTile tile : newOccupiedTiles) {
                for (RoomUnit _roomUnit : room.getRoomUnitManager().getRoomUnitsAt(tile)) {
                    hasRoomUnits = true;
                    if(_roomUnit.getCurrentPosition() == newTargetTile) {
                        Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.COLLISION, _roomUnit, room, new Object[]{item}));
                        break;
                    }
                }
            }

            if (newTargetTile != null && newTargetTile.getState() != RoomTileState.INVALID && room.getRoomItemManager().furnitureFitsAt(targetTile, item, item.getRotation(), false) == FurnitureMovementError.NONE) {
                if (!hasRoomUnits) {
                    RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
                    double oldZ = item.getZ();
                    if (room.getRoomItemManager().moveItemTo(item, newTargetTile, item.getRotation(), null, false, true) == FurnitureMovementError.NONE) {
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

    private RoomRotation nextDirection(RoomRotation currentDirection) {
        return switch (this.getWiredSettings().getIntegerParams().get(PARAM_BLOCKED_ACTION)) {
            case ACTION_TURN_BACK -> RoomRotation.fromValue(currentDirection.getValue()).getOpposite();
            case ACTION_TURN_LEFT_45 -> RoomRotation.counterClockwise(currentDirection);
            case ACTION_TURN_LEFT_90 -> RoomRotation.counterClockwise(RoomRotation.counterClockwise(currentDirection));
            case ACTION_TURN_RIGHT_45 -> RoomRotation.clockwise(currentDirection);
            case ACTION_TURN_RIGHT_90 -> RoomRotation.clockwise(RoomRotation.clockwise(currentDirection));
            case ACTION_TURN_RANDOM -> RoomRotation.fromValue(Emulator.getRandom().nextInt(8));
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
