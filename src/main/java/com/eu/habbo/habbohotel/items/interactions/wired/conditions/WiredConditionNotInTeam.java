package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotInTeam extends InteractionWiredCondition {
    public final int PARAM_TEAM = 0;
    private final GameTeamColors DEFAULT_TEAM = GameTeamColors.RED;

    public WiredConditionNotInTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionNotInTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int teamValue = this.getWiredSettings().getIntegerParams().get(PARAM_TEAM);

        if(teamValue < 1 || teamValue > 4) {
            return false;
        }

        GameTeamColors teamColor = GameTeamColors.values()[teamValue];

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            return habbo.getHabboInfo().getGamePlayer() == null || !habbo.getHabboInfo().getGamePlayer().getTeamColor().equals(teamColor);
        }

        return true;
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
        return WiredConditionType.NOT_ACTOR_IN_TEAM;
    }
}
