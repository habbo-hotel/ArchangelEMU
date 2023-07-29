package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.IWiredInteraction;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.OneWayDoorStatusMessageComposer;
import com.eu.habbo.messages.outgoing.wired.WiredConditionDataComposer;
import com.eu.habbo.messages.outgoing.wired.WiredEffectDataComposer;
import com.eu.habbo.messages.outgoing.wired.WiredTriggerDataComposer;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private WiredSettings wiredSettings;
    private long cooldown;
    private final TLongLongHashMap userExecutionCache = new TLongLongHashMap(3);

    public InteractionWired(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.wiredSettings = new WiredSettings();
        this.setExtraData("0");
    }

    InteractionWired(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.wiredSettings = new WiredSettings();
        this.setExtraData("0");
    }

    public abstract boolean execute(RoomUnit roomUnit, Room room, Object[] stuff);

    /**
     * On Room Loading run this, get Wired Data from Database and save it into the item
     *
     * @param set
     * @throws SQLException
     */
    public void loadWiredSettings(ResultSet set) throws SQLException, JsonProcessingException {
        String wiredData = set.getString("wired_data");
        this.wiredSettings = new WiredSettings();

        if(wiredData.startsWith("{")) {
            this.wiredSettings = WiredHandler.getObjectMapper().readValue(wiredData, WiredSettings.class);
        }
    }

    /**
     *
     * When double-clicking into the wired, verify items first and load its default parameters
     * then create a composer based on it's Wired Settings
     *
     * @param client
     * @param room
     * @param objects
     * @throws Exception
     */
    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        this.wiredSettings.getItems(room);

        if (client != null) {
            if (room.getRoomRightsManager().hasRights(client.getHabbo())) {
                MessageComposer composer = null;
                if(this instanceof InteractionWiredEffect) {
                    composer = new WiredEffectDataComposer((InteractionWiredEffect) this, room);
                } else if(this instanceof  InteractionWiredCondition) {
                    composer = new WiredConditionDataComposer((InteractionWiredCondition) this, room);
                } else if(this instanceof  InteractionWiredTrigger) {
                    composer = new WiredTriggerDataComposer((InteractionWiredTrigger) this, room);
                }

                client.sendResponse(composer);
                this.activateBox(room);
            }
        }
    }

    /**
     * When click save changes on the wired this executes, reads all the packet
     * And updates wired current Wired Settings
     *
     * @param packet
     */
    public void saveWiredSettings(ClientMessage packet, Room room) {
        int intParamCount = packet.readInt();
        List<Integer> integerParams = new ArrayList<>();

        for(int i = 0; i < intParamCount; i++)
        {
            integerParams.add(packet.readInt());
        }

        this.wiredSettings.setIntegerParams(integerParams);
        this.wiredSettings.setStringParam(packet.readString());

        int itemCount = packet.readInt();
        List<Integer> itemIds = new ArrayList<>();

        for(int i = 0; i < itemCount; i++)
        {
            itemIds.add(packet.readInt());
        }

        this.wiredSettings.setItemIds(itemIds);

        if(this instanceof InteractionWiredEffect) {
            this.wiredSettings.setDelay(packet.readInt());
        }

        this.wiredSettings.setSelectionType(packet.readInt());

        saveAdditionalData(room);
    }

    /**
     * This is executed on 3 different situations
     * When finishing executing: `saveWiredSettings`, when placing this item on floor, and when picking it up.
     * This what it does is converts Wired Settings into a JSON string
     * and updates wired_data in the database
     */
    @Override
    public void run() {
        if (this.needsUpdate()) {
            //TODO HERE IS WHERE WIRED_SAVE_EXCEPTION WILL BE THROWN
            //EXAMPLE: if StringParam should be number, throw error here, maybe activating a flag in wiredSettings that string params are numbers
            this.loadDefaultIntegerParams();

            String wiredData;

            try {
                wiredData = WiredHandler.getObjectMapper().writeValueAsString(this.wiredSettings);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(wiredData.equalsIgnoreCase("{}")) {
                wiredData = "";
            }

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

    /**
     * When picking up the wired, all its settings are erased and updated in database
     *
     * @param room
     */
    @Override
    public void onPickUp(Room room) {
        this.wiredSettings.dispose();
    }

    public void loadDefaultIntegerParams() {}
    public void saveAdditionalData(Room room) {}

    public void activateBox(Room room) {
        this.activateBox(room, null, 0L);
    }

    public void activateBox(Room room, RoomUnit roomUnit, long millis) {
        this.setExtraData(this.getExtraData().equals("1") ? "0" : "1");
        room.sendComposer(new OneWayDoorStatusMessageComposer(this).compose());
        if (roomUnit != null) {
            this.addUserExecutionCache(roomUnit.getVirtualId(), millis);
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
}
