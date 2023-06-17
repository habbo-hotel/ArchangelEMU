package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredInteraction;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredExclusionStrategy;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.OneWayDoorStatusMessageComposer;
import gnu.trove.map.hash.TLongLongHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class InteractionWired extends InteractionDefault implements IWiredInteraction {
    @Getter
    @Setter
    private String wiredData;

    @Getter
    @Setter
    private WiredSettings wiredSettings;

    private long cooldown;
    private final TLongLongHashMap userExecutionCache = new TLongLongHashMap(3);

    public InteractionWired(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.wiredData = "";
        this.wiredSettings = new WiredSettings();
        this.setExtradata("0");
    }

    InteractionWired(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.wiredData = "";
        this.wiredSettings = new WiredSettings();
        this.setExtradata("0");
    }

    public abstract boolean execute(RoomUnit roomUnit, Room room, Object[] stuff);

    @Override
    public void run() {
        if (this.needsUpdate()) {
            //TODO HERE IS WERE WIRED_SAVE_EXCEPTION WILL BE THROWN
            //EXAMPLE: if StringParam should be number, throw error here, maybe activating a flag in wiredSettings that string params are numbers

            WiredExclusionStrategy exclusionStrategy = new WiredExclusionStrategy(this.wiredSettings);

            String wiredData = WiredHandler.getGsonBuilder().setExclusionStrategies(exclusionStrategy).create().toJson(this.wiredSettings);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE items SET wired_data = ? WHERE id = ?")) {
                if (this.getRoomId() != 0) {
                    statement.setString(1, wiredData);
                } else {
                    statement.setString(1, "");
                }
                statement.setInt(2, this.getId());
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
        super.run();
    }

    @Override
    public void onPickUp(Room room) {
        this.wiredSettings = null;
        //TODO not sure about this
    }

    public void activateBox(Room room) {
        this.activateBox(room, null, 0L);
    }

    public void activateBox(Room room, RoomUnit roomUnit, long millis) {
        this.setExtradata(this.getExtradata().equals("1") ? "0" : "1");
        room.sendComposer(new OneWayDoorStatusMessageComposer(this).compose());
        if (roomUnit != null) {
            this.addUserExecutionCache(roomUnit.getId(), millis);
        }
    }

    protected long requiredCooldown() {
        return 50L;
    }

    public boolean canExecute(long newMillis) {
        return newMillis - this.cooldown >= this.requiredCooldown();
    }

    public void setCooldown(long newMillis) {
        this.cooldown = newMillis;
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    public boolean userCanExecute(int roomUnitId, long timestamp) {
        if (roomUnitId != -1) {
            if (this.userExecutionCache.containsKey(roomUnitId)) {
                long lastTimestamp = this.userExecutionCache.get(roomUnitId);
                return timestamp - lastTimestamp >= 100L;
            }

        }
        return true;
    }

    public void clearUserExecutionCache() {
        this.userExecutionCache.clear();
    }

    public void addUserExecutionCache(int roomUnitId, long timestamp) {
        this.userExecutionCache.put(roomUnitId, timestamp);
    }

    public void loadWiredSettings(ClientMessage packet, boolean isWiredEffect) {
        WiredSettings settings = new WiredSettings();

        int intParamCount = packet.readInt();
        List<Integer> integerParams = new ArrayList<>();

        for(int i = 0; i < intParamCount; i++)
        {
            integerParams.add(packet.readInt());
        }

        settings.setIntegerParams(integerParams);
        settings.setStringParam(packet.readString());

        int itemCount = packet.readInt();
        List<Integer> itemIds = new ArrayList<>();

        for(int i = 0; i < itemCount; i++)
        {
            itemIds.add(packet.readInt());
        }

        settings.setItemIds(itemIds);

        if(isWiredEffect)
        {
            settings.setDelay(packet.readInt());
        }

        settings.setSelectionType(packet.readInt());

        this.wiredSettings = settings;
    }

    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        WiredSettings settings = new WiredSettings();

        if(wiredData.startsWith("{")) {
            settings = WiredHandler.getGsonBuilder().create().fromJson(wiredData, WiredSettings.class);
        }

        this.wiredSettings = settings;
    }
}
