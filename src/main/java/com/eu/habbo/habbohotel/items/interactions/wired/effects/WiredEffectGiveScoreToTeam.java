package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import gnu.trove.map.hash.TIntIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveScoreToTeam extends InteractionWiredEffect {
    public final int PARAM_SCORE = 0;
    public final int PARAM_TIMES_PER_GAME = 1;
    public final int PARAM_TEAM = 2;
    private final TIntIntHashMap startTimes = new TIntIntHashMap();

    private final GameTeamColors DEFAULT_TEAM = GameTeamColors.RED;

    public WiredEffectGiveScoreToTeam(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    public WiredEffectGiveScoreToTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int score = this.getWiredSettings().getIntegerParams().get(PARAM_SCORE);
        int timesPerGame = this.getWiredSettings().getIntegerParams().get(PARAM_TIMES_PER_GAME);
        int teamValue = this.getWiredSettings().getIntegerParams().get(PARAM_TEAM);

        if(score < 1 || score > 100) {
            return false;
        }

        if(timesPerGame < 1 || timesPerGame > 10) {
            return false;
        }

        if(teamValue < 1 || teamValue > 4) {
            return false;
        }

        GameTeamColors teamColor = GameTeamColors.values()[teamValue];

        for (Game game : room.getGames()) {
            if (game != null && game.state.equals(GameState.RUNNING)) {
                int c = this.startTimes.get(game.getStartTime());

                if (c < timesPerGame) {
                    GameTeam team = game.getTeam(teamColor);

                    if (team != null) {
                        team.addTeamScore(score);
                        this.startTimes.put(game.getStartTime(), c + 1);
                    }
                }
            }
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
    public WiredEffectType getType() {
        return WiredEffectType.GIVE_SCORE_TEAM;
    }
}
