package com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.HabboInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFootballScoreboardYellow extends InteractionFootballScoreboard {
    public InteractionFootballScoreboardYellow(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem, GameTeamColors.YELLOW);
    }

    public InteractionFootballScoreboardYellow(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells, GameTeamColors.YELLOW);
    }
}