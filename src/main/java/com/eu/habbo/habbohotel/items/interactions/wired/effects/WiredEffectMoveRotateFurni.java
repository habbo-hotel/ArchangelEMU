package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WiredEffectMoveRotateFurni extends InteractionWiredEffect {


    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectMoveRotateFurni.class);

    public static final WiredEffectType type = WiredEffectType.MOVE_ROTATE;
    private final THashSet<HabboItem> items = new THashSet<>(WiredHandler.MAXIMUM_FURNI_SELECTION / 2);
    private int direction;
    private int rotation;

    public WiredEffectMoveRotateFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveRotateFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        // remove items that are no longer in the room
        this.items.removeIf(item -> Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null);

        THashSet<RoomTile> tilesToUpdate = new THashSet<>(Math.min(this.items.size(), 10));

        for (HabboItem item : this.items) {
            //Handle rotation
            if (this.rotation > 0) {
                tilesToUpdate.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
                int newRotation = this.getNewRotation(item);

                //Verify if rotation result in a valid position
                FurnitureMovementError rotateError = room.furnitureFitsAt(room.getLayout().getTile(item.getX(), item.getY()), item, newRotation);
                if (item.getRotation() != newRotation && (rotateError.equals(FurnitureMovementError.TILE_HAS_HABBOS) || rotateError.equals(FurnitureMovementError.TILE_HAS_PETS) ||
                        rotateError.equals(FurnitureMovementError.TILE_HAS_BOTS) || rotateError.equals(FurnitureMovementError.NONE))) {
                    item.setRotation(newRotation);
                    if (this.direction == 0) {
                        tilesToUpdate.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
                        room.sendComposer(new FloorItemUpdateComposer(item).compose());
                        for (RoomTile t : tilesToUpdate) {
                            room.updateHabbosAt(t.x, t.y);
                            room.updateBotsAt(t.x, t.y);
                        }
                    }
                }
            }

            //handle movement
            if (this.direction > 0) {
                RoomUserRotation moveDirection = this.getMovementDirection();
                boolean validMove;
                RoomLayout layout = room.getLayout();
                if (layout == null) return false;

                RoomTile newTile = layout.getTile(
                        (short) (item.getX() + ((moveDirection == RoomUserRotation.WEST || moveDirection == RoomUserRotation.NORTH_WEST || moveDirection == RoomUserRotation.SOUTH_WEST) ? -1 : (((moveDirection == RoomUserRotation.EAST || moveDirection == RoomUserRotation.SOUTH_EAST || moveDirection == RoomUserRotation.NORTH_EAST) ? 1 : 0)))),
                        (short) (item.getY() + ((moveDirection == RoomUserRotation.NORTH || moveDirection == RoomUserRotation.NORTH_EAST || moveDirection == RoomUserRotation.NORTH_WEST) ? 1 : ((moveDirection == RoomUserRotation.SOUTH || moveDirection == RoomUserRotation.SOUTH_EAST || moveDirection == RoomUserRotation.SOUTH_WEST) ? -1 : 0)))
                );

                if (newTile != null) {
                    boolean hasRoomUnits = false;
                    for (RoomUnit _roomUnit : room.getHabbosAndBotsAt(newTile)) {
                        hasRoomUnits = true;
                        WiredHandler.handle(WiredTriggerType.COLLISION, _roomUnit, room, new Object[]{item});
                    }

                    if (!hasRoomUnits && room.getStackHeight(newTile.x, newTile.y, true, item) != Short.MAX_VALUE) {
                        java.awt.Rectangle rectangle = new Rectangle(newTile.x,
                                newTile.y,
                                item.getBaseItem().getWidth(),
                                item.getBaseItem().getLength());

                        double offset = -Short.MAX_VALUE;
                        validMove = true;
                        for (short x = (short) rectangle.x; x < rectangle.x + rectangle.getWidth(); x++) {
                            if (!validMove) {
                                break;
                            }

                            for (short y = (short) rectangle.y; y < rectangle.y + rectangle.getHeight(); y++) {
                                RoomTile tile = layout.getTile(x, y);
                                if (tile == null || tile.state == RoomTileState.INVALID || !tile.getAllowStack()) {
                                    validMove = false;
                                    break;
                                }

                                THashSet<HabboItem> itemsAtNewTile = room.getItemsAt(tile);
                                if (item instanceof InteractionRoller && !itemsAtNewTile.isEmpty()) {
                                    validMove = false;
                                    break;
                                }

                                ArrayList<Pair<RoomTile, THashSet<HabboItem>>> tileItems = new ArrayList<>(rectangle.width * rectangle.height);
                                tileItems.add(Pair.create(tile, itemsAtNewTile));
                                if (!item.canStackAt(room, tileItems)) {
                                    validMove = false;
                                    break;
                                }

                                HabboItem i = room.getTopItemAt(x, y, item);
                                if (i != null && !i.getBaseItem().allowStack()) {
                                    validMove = false;
                                    break;
                                }

                                offset = Math.max(room.getStackHeight(newTile.x, newTile.y, false, item) - item.getZ(), offset);

                                tilesToUpdate.add(tile);
                            }
                        }
                        if (item.getZ() + offset > 40) {
                            offset = 40 - item.getZ();
                        }

                        if (validMove) {
                            if (this.rotation > 0) {
                                item.setX(newTile.x);
                                item.setY(newTile.y);
                                item.setZ(item.getZ() + offset);
                                room.sendComposer(new FloorItemUpdateComposer(item).compose());
                                for (RoomTile t : tilesToUpdate) {
                                    room.updateHabbosAt(t.x, t.y);
                                    room.updateBotsAt(t.x, t.y);
                                }
                            } else {
                                room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, offset, room).compose());
                            }
                        }
                    }
                }
            }
        }

        if (!tilesToUpdate.isEmpty()) {
            room.updateTiles(tilesToUpdate);
        }

        return true;
    }

    @Override
    public String getWiredData() {
        THashSet<HabboItem> items = new THashSet<>(this.items.size() / 2);

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || (room != null && room.getHabboItem(item.getId()) == null))
                items.add(item);
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }

        StringBuilder data = new StringBuilder(this.direction + "\t" +
                this.rotation + "\t" +
                this.getDelay() + "\t");

        for (HabboItem item : this.items) {
            data.append(item.getId()).append("\r");
        }

        return data.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items.clear();

        String[] data = set.getString("wired_data").split("\t");

        if (data.length == 4) {
            try {
                this.direction = Integer.parseInt(data[0]);
                this.rotation = Integer.parseInt(data[1]);
                this.setDelay(Integer.parseInt(data[2]));
            } catch (Exception e) {
                System.out.println(e);
            }

            for (String s : data[3].split("\r")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));

                if (item != null)
                    this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.direction = 0;
        this.rotation = 0;
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<>(this.items.size() / 2);

        for (HabboItem item : this.items) {
            if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for (HabboItem item : items) {
            this.items.remove(item);
        }

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items)
            message.appendInt(item.getId());
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
        message.appendInt(this.direction);
        message.appendInt(this.rotation);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            return false;

        packet.readInt();

        this.direction = packet.readInt();
        this.rotation = packet.readInt();

        packet.readString();

        int count = packet.readInt();
        if (count > Emulator.getConfig().getInt("hotel.wired.furni.selection.count", 5)) return false;

        this.items.clear();
        for (int i = 0; i < count; i++) {
            this.items.add(room.getHabboItem(packet.readInt()));
        }

        this.setDelay(packet.readInt());

        return true;
    }


    /**
     * Returns a new rotation for an item based on the wired options
     *
     * @param item HabboItem
     * @return new rotation
     */
    private int getNewRotation(HabboItem item) {
        int rotationToAdd = 0;
        if (this.rotation == 1) {
            rotationToAdd = 2;
        } else if (this.rotation == 2) {
            rotationToAdd = 6;
        }
        //Random rotation
        else if (this.rotation == 3) {
            if (Emulator.getRandom().nextInt(2) == 1) {
                rotationToAdd = 2;
            } else {
                rotationToAdd = 6;
            }
        }

        return ((item.getRotation() + rotationToAdd) % 8) % (item.getBaseItem().getWidth() > 1 || item.getBaseItem().getLength() > 1 ? 4 : 8);
    }

    /**
     * Returns the direction of movement based on the wired settings
     *
     * @return direction
     */
    private RoomUserRotation getMovementDirection() {
        RoomUserRotation movemementDirection = RoomUserRotation.NORTH;
        if (this.direction == 1) {
            movemementDirection = RoomUserRotation.values()[Emulator.getRandom().nextInt(RoomUserRotation.values().length / 2) * 2];
        } else if (this.direction == 2) {
            if (Emulator.getRandom().nextInt(2) == 1) {
                movemementDirection = RoomUserRotation.EAST;
            } else {
                movemementDirection = RoomUserRotation.WEST;
            }
        } else if (this.direction == 3) {
            if (Emulator.getRandom().nextInt(2) != 1) {
                movemementDirection = RoomUserRotation.SOUTH;
            }
        } else if (this.direction == 4) {
            movemementDirection = RoomUserRotation.SOUTH;
        } else if (this.direction == 5) {
            movemementDirection = RoomUserRotation.EAST;
        } else if (this.direction == 7) {
            movemementDirection = RoomUserRotation.WEST;
        }
        return movemementDirection;
    }
}