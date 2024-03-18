package com.eu.habbo.habbohotel.items.interactions.games.football.goals;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFootballGoalRed extends InteractionFootballGoal {
    public InteractionFootballGoalRed(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem, GameTeamColors.RED);
    }

    public InteractionFootballGoalRed(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells, GameTeamColors.RED);
    }
}