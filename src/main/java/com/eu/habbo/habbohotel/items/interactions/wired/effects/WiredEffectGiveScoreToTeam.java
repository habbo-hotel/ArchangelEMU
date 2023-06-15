package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.map.hash.TIntIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveScoreToTeam extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.GIVE_SCORE_TEAM;

    private int points;
    private int count;
    private GameTeamColors teamColor = GameTeamColors.RED;

    private final TIntIntHashMap startTimes = new TIntIntHashMap();

    public WiredEffectGiveScoreToTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public WiredEffectGiveScoreToTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        for (Game game : room.getGames()) {
            if (game != null && game.state.equals(GameState.RUNNING)) {
                int c = this.startTimes.get(game.getStartTime());

                if (c < this.count) {
                    GameTeam team = game.getTeam(this.teamColor);

                    if (team != null) {
                        team.addTeamScore(this.points);

                        this.startTimes.put(game.getStartTime(), c + 1);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.points, this.count, this.teamColor, this.getWiredSettings().getDelay()));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if(wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.points = data.score;
            this.count = data.count;
            this.teamColor = data.team;
            this.getWiredSettings().setDelay(data.delay);
        }
        else {
            String[] data = set.getString("wired_data").split(";");

            if (data.length == 4) {
                this.points = Integer.parseInt(data[0]);
                this.count = Integer.parseInt(data[1]);
                this.teamColor = GameTeamColors.values()[Integer.parseInt(data[2])];
                this.getWiredSettings().setDelay(Integer.parseInt(data[3]));
            }

            this.needsUpdate(true);
        }
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean saveData() throws WiredSaveException {
        if(this.getWiredSettings().getIntegerParams().length < 3) throw  new WiredSaveException("Invalid data");

        int points = this.getWiredSettings().getIntegerParams()[0];

        if(points < 1 || points > 100)
            throw new WiredSaveException("Points is invalid");

        int timesPerGame = this.getWiredSettings().getIntegerParams()[1];

        if(timesPerGame < 1 || timesPerGame > 10)
            throw new WiredSaveException("Times per game is invalid");

        int team = this.getWiredSettings().getIntegerParams()[2];

        if(team < 1 || team > 4)
            throw new WiredSaveException("Team is invalid");

        int delay = this.getWiredSettings().getDelay();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.points = points;
        this.count = timesPerGame;
        this.teamColor = GameTeamColors.values()[team];
        this.getWiredSettings().setDelay(delay);

        return true;
    }

    static class JsonData {
        int score;
        int count;
        GameTeamColors team;
        int delay;

        public JsonData(int score, int count, GameTeamColors team, int delay) {
            this.score = score;
            this.count = count;
            this.team = team;
            this.delay = delay;
        }
    }
}
