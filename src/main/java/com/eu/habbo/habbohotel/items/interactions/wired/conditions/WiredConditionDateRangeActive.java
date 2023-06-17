package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a wired condition item in the game "Habbo Hotel". It checks if the current date is within
 * a given range.
 */
public class WiredConditionDateRangeActive extends InteractionWiredCondition {
    public final int PARAM_START_DATE = 0;
    public final int PARAM_END_DATE = 1;

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

        int startDate = this.getWiredSettings().getIntegerParams().get(PARAM_START_DATE);
        int endDate = this.getWiredSettings().getIntegerParams().get(PARAM_END_DATE);

        return startDate < time && endDate >= time;
    }

    @Override
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    /**
     * Returns the {@link WiredConditionType} of this object.
     * @return the type of this wired condition
     */
    @Override
    public WiredConditionType getType() {
        return WiredConditionType.DATE_RANGE;
    }
}
