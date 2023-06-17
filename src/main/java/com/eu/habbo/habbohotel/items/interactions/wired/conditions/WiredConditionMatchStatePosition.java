package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.interfaces.InteractionWiredMatchFurniSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WiredConditionMatchStatePosition extends InteractionWiredCondition implements InteractionWiredMatchFurniSettings {
    public final int PARAM_STATE = 0;
    public final int PARAM_ROTATION = 1;
    public final int PARAM_POSITION = 2;

    @Getter
    private THashSet<WiredMatchFurniSetting> matchFurniSettings;

    public WiredConditionMatchStatePosition(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionMatchStatePosition(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        boolean state = this.getWiredSettings().getIntegerParams().get(PARAM_STATE) == 1;
        boolean position = this.getWiredSettings().getIntegerParams().get(PARAM_POSITION) == 1;
        boolean rotation = this.getWiredSettings().getIntegerParams().get(PARAM_ROTATION) == 1;

        for(HabboItem item : this.getWiredSettings().getItems(room)) {
            WiredMatchFurniSetting setting = new WiredMatchFurniSetting(item.getId(), item.getExtradata(), item.getRotation(), item.getX(), item.getY());

            this.matchFurniSettings.add(setting);

            if(state) {
                if(!item.getExtradata().equals(setting.getState())) {
                    return false;
                }
            }

            if(position) {
                if (!(setting.getX() == item.getX() && setting.getY() == item.getY())) {
                    return false;
                }
            }

            if(rotation) {
                if (setting.getRotation() != item.getRotation()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public boolean shouldMatchState() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_STATE) == 1;
    }

    @Override
    public boolean shouldMatchRotation() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_ROTATION) == 1;
    }

    @Override
    public boolean shouldMatchPosition() {
        return this.getWiredSettings().getIntegerParams().get(PARAM_POSITION) == 1;
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.MATCH_SSHOT;
    }
}
