package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a wired condition item in the game "Habbo Hotel". It checks if the current date is within
 * a given range.
 */
public class WiredConditionDateRangeActive extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.DATE_RANGE;

    private int startDate; // the start of the date range
    private int endDate; // the end of the date range

    /**
     * Creates a new instance of this class.
     * @param set the ResultSet object to get data from
     * @param baseItem the base item for this wired condition
     * @throws SQLException if an error occurs while getting data from the ResultSet object
     */
    public WiredConditionDateRangeActive(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    /**
     * Creates a new instance of this class.
     * @param id The ID of this item.
     * @param userId The ID of the user that owns this item.
     * @param item The item this instance is associated with.
     * @param extradata Additional data associated with this item.
     * @param limitedStack The amount of items in this stack (if this item is stackable).
     * @param limitedSells The amount of items that can be sold from this stack (if this item is sellable).
     */
    public WiredConditionDateRangeActive(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    /**
     * Returns the {@link WiredConditionType} of this object.
     * @return the type of this wired condition
     */
    @Override
    public WiredConditionType getType() {
        return type;
    }

    /**
     * Sends information about this wired condition to the client.
     * @param message the message to send data with
     * @param room the room this wired condition is in
     */
    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
        message.appendInt(this.startDate);
        message.appendInt(this.endDate);
        message.appendInt(0);
        message.appendInt(this.getType().getCode());
        message.appendInt(this.startDate);
        message.appendInt(this.endDate);
    }

    /**
     * Saves the given {@link WiredSettings} object to this wired condition.
     * @param settings the settings to save
     * @return {@code true} if the settings were saved successfully, {@code false} otherwise
     * */
    @Override
    public boolean saveData(WiredSettings settings) {
        if(settings.getIntParams().length < 2) return false;
        this.startDate = settings.getIntParams()[0];
        this.endDate = settings.getIntParams()[1];
        return true;
    }

    /**
     * Determines if the wired condition is met.
     * @param roomUnit the room unit that triggered the condition
     * @param room the room that the condition is in
     * @param stuff additional data for the condition
     * @return true if the current time is within the given date range (startDate is less than the current time and
     * endDate is greater than or equal to the current time)
     */
    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int time = Emulator.getIntUnixTimestamp();
        return this.startDate < time && this.endDate >= time;
    }

    /**
     * Gets the wired data for this wired condition in JSON format.
     * @return the wired data in JSON format
     */
    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.startDate,
                this.endDate
        ));
    }

    /**
     * Loads the wired data for this wired condition from a database.
     * @param set the ResultSet object to get data from
     * @param room the room that this wired condition is in
     * @throws SQLException if an error occurs while getting data from the ResultSet object
     */
    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.startDate = data.startDate;
            this.endDate = data.endDate;
        } else {
            String[] data = wiredData.split("\t");

            if (data.length == 2) {
                try {
                    this.startDate = Integer.parseInt(data[0]);
                    this.endDate = Integer.parseInt(data[1]);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Called when this item is picked up. Resets the startDate and endDate member variables to 0.
     */
    @Override
    public void onPickUp() {
        this.startDate = 0;
        this.endDate = 0;
    }

    /**
     * A nested class for storing the wired data for this wired condition in JSON format.
     */
    static class JsonData {
        int startDate;
        int endDate;

        /**
         * Creates a new instance of this class.
         * @param startDate the start of the date range
         * @param endDate the end of the date range
         */
        public JsonData(int startDate, int endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
