package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WiredEffectMatchFurni extends InteractionWiredEffect {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectMatchFurni.class);

    private static final WiredEffectType type = WiredEffectType.MATCH_SSHOT;
    public boolean checkForWiredResetPermission = true;
    private THashSet<WiredMatchFurniSetting> settings;
    private boolean state = false;
    private boolean direction = false;
    private boolean position = false;

    public WiredEffectMatchFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.settings = new THashSet<>(0);
    }

    public WiredEffectMatchFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.settings = new THashSet<>(0);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<RoomTile> tilesToUpdate = new THashSet<>(this.settings.size());
        //this.refresh();

        if(this.settings.isEmpty())
            return false;

        for (WiredMatchFurniSetting setting : this.settings) {
            HabboItem item = room.getHabboItem(setting.itemId);
            if (item != null) {
                if (this.state && (this.checkForWiredResetPermission && item.allowWiredResetState())) {
                    if (!setting.state.equals(" ")) {
                        item.setExtradata(setting.state);
                        room.updateItemState(item);
                        tilesToUpdate.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
                    }
                }

                int oldRotation = item.getRotation();
                boolean slideAnimation = true;
                double offsetZ = 0;

                if (this.direction && item.getRotation() != setting.rotation) {
                    item.setRotation(setting.rotation);
                    slideAnimation = false;

                    room.scheduledTasks.add(() -> {
                        room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), oldRotation).forEach(t -> {
                            room.updateBotsAt(t.x, t.y);
                            room.updateHabbosAt(t.x, t.y);
                        });
                        room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), setting.rotation).forEach(t -> {
                            room.updateBotsAt(t.x, t.y);
                            room.updateHabbosAt(t.x, t.y);
                        });
                    });
                }

                RoomTile t = null;

                if (this.position) {
                    t = room.getLayout().getTile((short) setting.x, (short) setting.y);

                    if (t != null && t.state != RoomTileState.INVALID) {
                        boolean canMove = true;

                        if (t.x == item.getX() && t.y == item.getY() || room.hasHabbosAt(t.x, t.y)) {
                            canMove = !(room.getTopItemAt(t.x, t.y) == item);
                            slideAnimation = false;
                        }


                        if (canMove && !room.hasHabbosAt(t.x, t.y)) {
                            THashSet<RoomTile> tiles = room.getLayout().getTilesAt(t, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), setting.rotation);
                            double highestZ = -1d;
                            for (RoomTile tile : tiles) {
                                if (tile.state == RoomTileState.INVALID) {
                                    highestZ = -1d;
                                    break;
                                }

                                if (item instanceof InteractionRoller && room.hasItemsAt(tile.x, tile.y)) {
                                    highestZ = -1d;
                                    break;
                                }

                                double stackHeight = room.getStackHeight(tile.x, tile.y, false, item);
                                if (stackHeight > highestZ) {
                                    highestZ = stackHeight;
                                }
                            }

                            if (highestZ != -1d) {
                                tilesToUpdate.addAll(tiles);

                                offsetZ = highestZ - item.getZ();
                                double totalHeight = item.getZ() + offsetZ;
                                if (totalHeight > 40) break;
                                tilesToUpdate.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), oldRotation));

                                if (!slideAnimation) {
                                    item.setX(t.x);
                                    item.setY(t.y);
                                }
                            }
                        }
                    }
                }

                if (slideAnimation && t != null) {
                    room.sendComposer(new FloorItemOnRollerComposer(item, null, t, offsetZ, room).compose());
                } else {
                    room.updateItem(item);
                }

                item.needsUpdate(true);
            }
        }

        room.updateTiles(tilesToUpdate);

        return true;
    }

    @Override
    public String getWiredData() {
        this.refresh();

        StringBuilder data = new StringBuilder(this.settings.size() + ":");

        if (this.settings.isEmpty()) {
            data.append(";");
        } else {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

            for (WiredMatchFurniSetting item : this.settings) {
                HabboItem i;

                if (room != null) {
                    i = room.getHabboItem(item.itemId);

                    if (i != null) {
                        data.append(item.toString(this.checkForWiredResetPermission && i.allowWiredResetState())).append(";");
                    }
                }
            }
        }

        data.append(":").append(this.state ? 1 : 0).append(":").append(this.direction ? 1 : 0).append(":").append(this.position ? 1 : 0).append(":").append(this.getDelay());

        return data.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] data = set.getString("wired_data").split(":");

        int itemCount = Integer.parseInt(data[0]);

        String[] items = data[1].split(Pattern.quote(";"));

        for (int i = 0; i < items.length; i++) {
            try {

                String[] stuff = items[i].split(Pattern.quote("-"));

                if (stuff.length >= 5) {
                    this.settings.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4])));
                }

            } catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }

        this.state = data[2].equals("1");
        this.direction = data[3].equals("1");
        this.position = data[4].equals("1");
        this.setDelay(Integer.parseInt(data[5]));
    }

    @Override
    public void onPickUp() {
        this.settings.clear();
        this.state = false;
        this.direction = false;
        this.position = false;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        this.refresh();

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.settings.size());

        for (WiredMatchFurniSetting item : this.settings)
            message.appendInt(item.itemId);

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(3);
        message.appendInt(this.state ? 1 : 0);
        message.appendInt(this.direction ? 1 : 0);
        message.appendInt(this.position ? 1 : 0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) throws WiredSaveException {
        packet.readInt();

        boolean setState = packet.readInt() == 1;
        boolean setDirection = packet.readInt() == 1;
        boolean setPosition = packet.readInt() == 1;

        packet.readString();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            throw new WiredSaveException("Trying to save wired in unloaded room");

        int itemsCount = packet.readInt();

        if(itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }

        List<WiredMatchFurniSetting> newSettings = new ArrayList<>();

        for (int i = 0; i < itemsCount; i++) {
            int itemId = packet.readInt();
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);

            if(it == null)
                throw new WiredSaveException(String.format("Item %s not found", itemId));

            newSettings.add(new WiredMatchFurniSetting(it.getId(), this.checkForWiredResetPermission && it.allowWiredResetState() ? it.getExtradata() : " ", it.getRotation(), it.getX(), it.getY()));
        }

        int delay = packet.readInt();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.state = setState;
        this.direction = setDirection;
        this.position = setPosition;
        this.settings.clear();
        this.settings.addAll(newSettings);
        this.setDelay(delay);

        return true;
    }

    private void refresh() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room != null && room.isLoaded()) {
            THashSet<WiredMatchFurniSetting> remove = new THashSet<>();

            for (WiredMatchFurniSetting setting : this.settings) {
                HabboItem item = room.getHabboItem(setting.itemId);
                if (item == null) {
                    remove.add(setting);
                }
            }

            for (WiredMatchFurniSetting setting : remove) {
                this.settings.remove(setting);
            }
        }
    }
}
