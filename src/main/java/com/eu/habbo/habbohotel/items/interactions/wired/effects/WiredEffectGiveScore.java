package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

public class WiredEffectGiveScore extends InteractionWiredEffect {
    public final int PARAM_SCORE = 0;
    public final int PARAM_TIMES_PER_GAME = 1;
    private final TObjectIntMap<Map.Entry<Integer, Integer>> data = new TObjectIntHashMap<>();

    public WiredEffectGiveScore(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveScore(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int score = this.getWiredSettings().getIntegerParams().get(PARAM_SCORE);
        int timesPerGame = this.getWiredSettings().getIntegerParams().get(PARAM_TIMES_PER_GAME);

        if(score < 1 || score > 100) {
            return false;
        }

        if(timesPerGame < 1 || timesPerGame > 10) {
            return false;
        }

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null && habbo.getHabboInfo().getCurrentGame() != null) {
            Game game = room.getGame(habbo.getHabboInfo().getCurrentGame());

            if (game == null)
                return false;

            int gameStartTime = game.getStartTime();

            TObjectIntMap<Map.Entry<Integer, Integer>> dataClone = new TObjectIntHashMap<>(this.data);

            TObjectIntIterator<Map.Entry<Integer, Integer>> iterator = dataClone.iterator();

            for (int i = dataClone.size(); i-- > 0; ) {
                iterator.advance();

                Map.Entry<Integer, Integer> map = iterator.key();

                if (map.getValue() == habbo.getHabboInfo().getId()) {
                    if (map.getKey() == gameStartTime) {
                        if (iterator.value() < timesPerGame) {
                            iterator.setValue(iterator.value() + 1);

                            habbo.getHabboInfo().getGamePlayer().addScore(score, true);

                            return true;
                        }
                    } else {
                        iterator.remove();
                    }
                }
            }

            try {
                this.data.put(new AbstractMap.SimpleEntry<>(gameStartTime, habbo.getHabboInfo().getId()), 1);
            }
            catch(IllegalArgumentException ignored) {

            }


            if (habbo.getHabboInfo().getGamePlayer() != null) {
                habbo.getHabboInfo().getGamePlayer().addScore(score, true);
            }

            return true;
        }

        return false;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(1);
            this.getWiredSettings().getIntegerParams().add(1);
        }
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.GIVE_SCORE;
    }
}
