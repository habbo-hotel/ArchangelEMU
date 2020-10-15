package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.*;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class WiredEffectChangeFurniDirection extends InteractionWiredEffect {
    public static final int ACTION_WAIT = 0;
    public static final int ACTION_TURN_RIGHT_45 = 1;
    public static final int ACTION_TURN_RIGHT_90 = 2;
    public static final int ACTION_TURN_LEFT_45 = 3;
    public static final int ACTION_TURN_LEFT_90 = 4;
    public static final int ACTION_TURN_BACK = 5;
    public static final int ACTION_TURN_RANDOM = 6;

    public static final WiredEffectType type = WiredEffectType.MOVE_DIRECTION;

    private final THashMap<HabboItem, WiredChangeDirectionSetting> items = new THashMap<>(0);
    private RoomUserRotation startRotation = RoomUserRotation.NORTH;
    private int blockedAction = 0;

    public WiredEffectChangeFurniDirection(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectChangeFurniDirection(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<HabboItem> items = new THashSet<>();

        for (HabboItem item : this.items.keySet()) {
            if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }

        if (this.items.isEmpty()) return false;

        for (Map.Entry<HabboItem, WiredChangeDirectionSetting> entry : this.items.entrySet()) {
            HabboItem item = entry.getKey();
            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), entry.getValue().direction.getValue());

            int count = 1;
            while ((targetTile == null || targetTile.state == RoomTileState.INVALID || room.furnitureFitsAt(targetTile, item, item.getRotation(), false) != FurnitureMovementError.NONE) && count < 8) {
                entry.getValue().direction = this.nextRotation(entry.getValue().direction);

                RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), entry.getValue().direction.getValue());
                if (tile != null && tile.state != RoomTileState.INVALID) {
                    targetTile = tile;
                }

                count++;
            }
        }

        for (Map.Entry<HabboItem, WiredChangeDirectionSetting> entry : this.items.entrySet()) {
            HabboItem item = entry.getKey();
            int newDirection = entry.getValue().direction.getValue();

            RoomTile targetTile = room.getLayout().getTileInFront(room.getLayout().getTile(item.getX(), item.getY()), newDirection);

            if(item.getRotation() != entry.getValue().rotation) {
                if(room.furnitureFitsAt(targetTile, item, entry.getValue().rotation, false) != FurnitureMovementError.NONE)
                    continue;

                room.moveFurniTo(entry.getKey(), targetTile, entry.getValue().rotation, null, true);
            }

            if (targetTile != null && targetTile.state != RoomTileState.INVALID && room.furnitureFitsAt(targetTile, item, item.getRotation(), false) == FurnitureMovementError.NONE) {
                boolean hasRoomUnits = false;
                THashSet<RoomTile> newOccupiedTiles = room.getLayout().getTilesAt(targetTile, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                for(RoomTile tile : newOccupiedTiles) {
                    for (RoomUnit _roomUnit : room.getRoomUnits(tile)) {
                        hasRoomUnits = true;
                        if(_roomUnit.getCurrentLocation() == targetTile) {
                            Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.COLLISION, _roomUnit, room, new Object[]{entry.getKey()}));
                            break;
                        }
                    }
                }

                if (!hasRoomUnits) {
                    RoomTile oldLocation = room.getLayout().getTile(entry.getKey().getX(), entry.getKey().getY());
                    double oldZ = entry.getKey().getZ();
                    if(room.moveFurniTo(entry.getKey(), targetTile, item.getRotation(), null, false) == FurnitureMovementError.NONE) {
                        room.sendComposer(new FloorItemOnRollerComposer(entry.getKey(), null, oldLocation, oldZ, targetTile, entry.getKey().getZ(), 0, room).compose());
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String getWiredData() {
        StringBuilder data = new StringBuilder(this.getDelay() + "\t" + this.startRotation.getValue() + "\t" + this.blockedAction + "\t" + this.items.size());

        for (Map.Entry<HabboItem, WiredChangeDirectionSetting> entry : this.items.entrySet()) {
            data.append("\t").append(entry.getKey().getId()).append(":").append(entry.getValue().direction.getValue()).append(":").append(entry.getValue().rotation);
        }

        return data.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] data = set.getString("wired_data").split("\t");

        if (data.length >= 4) {
            this.setDelay(Integer.parseInt(data[0]));
            this.startRotation = RoomUserRotation.fromValue(Integer.parseInt(data[1]));
            this.blockedAction = Integer.parseInt(data[2]);

            int itemCount = Integer.parseInt(data[3]);

            if (itemCount > 0) {
                for (int i = 4; i < data.length; i++) {
                    String[] subData = data[i].split(":");

                    if (subData.length >= 2) {
                        HabboItem item = room.getHabboItem(Integer.parseInt(subData[0]));

                        if (item != null) {
                            int rotation = item.getRotation();

                            if(subData.length > 2) {
                                rotation = Integer.parseInt(subData[2]);
                            }

                            this.items.put(item, new WiredChangeDirectionSetting(item.getId(), rotation, RoomUserRotation.fromValue(Integer.parseInt(subData[1]))));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.setDelay(0);
        this.items.clear();
        this.blockedAction = 0;
        this.startRotation = RoomUserRotation.NORTH;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (Map.Entry<HabboItem, WiredChangeDirectionSetting> item : this.items.entrySet()) {
            message.appendInt(item.getKey().getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
        message.appendInt(this.startRotation.getValue());
        message.appendInt(this.blockedAction);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) throws WiredSaveException {
        packet.readInt();
        int startDirectionInt = packet.readInt();

        if(startDirectionInt < 0 || startDirectionInt > 7 || (startDirectionInt % 2) != 0) {
            throw new WiredSaveException("Start direction is invalid");
        }

        RoomUserRotation startDirection = RoomUserRotation.fromValue(startDirectionInt);

        int blockedActionInt = packet.readInt();

        if(blockedActionInt < 0 || blockedActionInt > 6) {
            throw new WiredSaveException("Blocked action is invalid");
        }

        packet.readString();

        int itemsCount = packet.readInt();

        if(itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }

        THashMap<HabboItem, WiredChangeDirectionSetting> newItems = new THashMap<>();

        for (int i = 0; i < itemsCount; i++) {
            int itemId = packet.readInt();
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);

            if(it == null)
                throw new WiredSaveException(String.format("Item %s not found", itemId));

            newItems.put(it, new WiredChangeDirectionSetting(it.getId(), it.getRotation(), startDirection));
        }

        int delay = packet.readInt();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.items.clear();
        this.items.putAll(newItems);
        this.startRotation = startDirection;
        this.blockedAction = blockedActionInt;
        this.setDelay(delay);

        return true;
    }

    private RoomUserRotation nextRotation(RoomUserRotation currentRotation) {
        switch (this.blockedAction) {
            case ACTION_TURN_BACK:
                return RoomUserRotation.fromValue(currentRotation.getValue() + 4);
            case ACTION_TURN_LEFT_45:
                return RoomUserRotation.counterClockwise(currentRotation);
            case ACTION_TURN_LEFT_90:
                return RoomUserRotation.counterClockwise(RoomUserRotation.counterClockwise(currentRotation));
            case ACTION_TURN_RIGHT_45:
                return RoomUserRotation.clockwise(currentRotation);
            case ACTION_TURN_RIGHT_90:
                return RoomUserRotation.clockwise(RoomUserRotation.clockwise(currentRotation));
            case ACTION_TURN_RANDOM:
                return RoomUserRotation.fromValue(Emulator.getRandom().nextInt(8));
            case ACTION_WAIT:
            default:
                return currentRotation;
        }
    }

    @Override
    protected long requiredCooldown() {
        return 495;
    }
}
