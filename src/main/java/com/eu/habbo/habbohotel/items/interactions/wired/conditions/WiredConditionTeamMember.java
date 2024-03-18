package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredConditionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionTeamMember extends InteractionWiredCondition {
    public final int PARAM_TEAM = 0;
    private final GameTeamColors DEFAULT_TEAM = GameTeamColors.RED;

    public WiredConditionTeamMember(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionTeamMember(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int teamValue = this.getWiredSettings().getIntegerParams().get(PARAM_TEAM);

        if(teamValue < 1 || teamValue > 4) {
            return false;
        }

        GameTeamColors teamColor = GameTeamColors.values()[teamValue];

        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

        if (habbo != null) {
            if (habbo.getHabboInfo().getGamePlayer() != null) {
                return habbo.getHabboInfo().getGamePlayer().getTeamColor().equals(teamColor);
            }
        }

        return false;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(1);
            this.getWiredSettings().getIntegerParams().add(1);
            this.getWiredSettings().getIntegerParams().add(DEFAULT_TEAM.type);
        }
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.ACTOR_IN_TEAM;
    }
}
