package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class WiredEffectMatchFurni extends InteractionWiredEffect implements InteractionWiredMatchFurniSettings {

    private static final WiredEffectType type = WiredEffectType.MATCH_SSHOT;
    private final boolean checkForWiredResetPermission = true;
    private final THashSet<WiredMatchFurniSetting> wiredMatchSettings;
    private boolean state = false;
    private boolean direction = false;
    private boolean position = false;

    public WiredEffectMatchFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.wiredMatchSettings = new THashSet<>(0);
    }

    public WiredEffectMatchFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.wiredMatchSettings = new THashSet<>(0);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {

        if(this.wiredMatchSettings.isEmpty())
            return true;

        for (WiredMatchFurniSetting setting : this.wiredMatchSettings) {
            HabboItem item = room.getHabboItem(setting.getItem_id());
            if (item != null) {
                if (this.state && (this.checkForWiredResetPermission && item.allowWiredResetState())) {
                    if (!setting.getState().equals(" ") && !item.getExtradata().equals(setting.getState())) {
                        item.setExtradata(setting.getState());
                        room.updateItemState(item);
                    }
                }

                RoomTile oldLocation = room.getLayout().getTile(item.getX(), item.getY());
                double oldZ = item.getZ();

                if(this.direction && !this.position) {
                    if(item.getRotation() != setting.getRotation() && room.furnitureFitsAt(oldLocation, item, setting.getRotation(), false) == FurnitureMovementError.NONE) {
                        room.moveFurniTo(item, oldLocation, setting.getRotation(), null, true);
                    }
                }
                else if(this.position) {
                    boolean slideAnimation = !this.direction || item.getRotation() == setting.getRotation();
                    RoomTile newLocation = room.getLayout().getTile((short) setting.getX(), (short) setting.getY());
                    int newRotation = this.direction ? setting.getRotation() : item.getRotation();

                    if(newLocation != null && newLocation.getState() != RoomTileState.INVALID && (newLocation != oldLocation || newRotation != item.getRotation()) && room.furnitureFitsAt(newLocation, item, newRotation, true) == FurnitureMovementError.NONE) {
                        if(room.moveFurniTo(item, newLocation, newRotation, null, !slideAnimation) == FurnitureMovementError.NONE) {
                            if(slideAnimation) {
                                room.sendComposer(new FloorItemOnRollerComposer(item, null, oldLocation, oldZ, newLocation, item.getZ(), 0, room).compose());
                            }
                        }
                    }
                }

            }
        }

        return true;
    }

    @Override
    public String getWiredData() {
        this.refresh();
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.state, this.direction, this.position, new ArrayList<>(this.wiredMatchSettings), this.getWiredSettings().getDelay()));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if(wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.getWiredSettings().setDelay(data.delay);
            this.state = data.state;
            this.direction = data.direction;
            this.position = data.position;
            this.wiredMatchSettings.clear();
            this.wiredMatchSettings.addAll(data.items);
        }
        else {
            String[] data = set.getString("wired_data").split(":");

            int itemCount = Integer.parseInt(data[0]);

            String[] items = data[1].split(Pattern.quote(";"));

            for (String item : items) {
                try {

                    String[] stuff = item.split(Pattern.quote("-"));

                    if (stuff.length >= 5) {
                        this.wiredMatchSettings.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4])));
                    }

                } catch (Exception e) {
                    log.error("Caught exception", e);
                }
            }

            this.state = data[2].equals("1");
            this.direction = data[3].equals("1");
            this.position = data[4].equals("1");
            this.getWiredSettings().setDelay(Integer.parseInt(data[5]));
            this.needsUpdate(true);
        }
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean saveData() throws WiredSaveException {
        if(this.getWiredSettings().getIntegerParams().length < 3) throw new WiredSaveException("Invalid data");
        boolean setState = this.getWiredSettings().getIntegerParams()[0] == 1;
        boolean setDirection = this.getWiredSettings().getIntegerParams()[1] == 1;
        boolean setPosition = this.getWiredSettings().getIntegerParams()[2] == 1;

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            throw new WiredSaveException("Trying to save wired in unloaded room");

        int itemsCount = this.getWiredSettings().getItems().length;

        if(itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }

        List<WiredMatchFurniSetting> newSettings = new ArrayList<>();

        for (int i = 0; i < itemsCount; i++) {
            int itemId = this.getWiredSettings().getItems()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);

            if(it == null)
                throw new WiredSaveException(String.format("Item %s not found", itemId));

            newSettings.add(new WiredMatchFurniSetting(it.getId(), this.checkForWiredResetPermission && it.allowWiredResetState() ? it.getExtradata() : " ", it.getRotation(), it.getX(), it.getY()));
        }

        int delay = this.getWiredSettings().getDelay();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.state = setState;
        this.direction = setDirection;
        this.position = setPosition;
        this.wiredMatchSettings.clear();
        this.wiredMatchSettings.addAll(newSettings);
        this.getWiredSettings().setDelay(delay);

        return true;
    }

    private void refresh() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room != null && room.isLoaded()) {
            THashSet<WiredMatchFurniSetting> remove = new THashSet<>();

            for (WiredMatchFurniSetting setting : this.wiredMatchSettings) {
                HabboItem item = room.getHabboItem(setting.getItem_id());
                if (item == null) {
                    remove.add(setting);
                }
            }

            for (WiredMatchFurniSetting setting : remove) {
                this.wiredMatchSettings.remove(setting);
            }

        }
    }

    @Override
    public THashSet<WiredMatchFurniSetting> getMatchFurniSettings() {
        return this.wiredMatchSettings;
    }

    @Override
    public boolean shouldMatchState() {
        return this.state;
    }

    @Override
    public boolean shouldMatchRotation() {
        return this.direction;
    }

    @Override
    public boolean shouldMatchPosition() {
        return this.position;
    }

    static class JsonData {
        boolean state;
        boolean direction;
        boolean position;
        List<WiredMatchFurniSetting> items;
        int delay;

        public JsonData(boolean state, boolean direction, boolean position, List<WiredMatchFurniSetting> items, int delay) {
            this.state = state;
            this.direction = direction;
            this.position = position;
            this.items = items;
            this.delay = delay;
        }
    }
}
