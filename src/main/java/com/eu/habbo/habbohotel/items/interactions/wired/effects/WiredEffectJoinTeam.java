package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectJoinTeam extends InteractionWiredEffect {
    public final int PARAM_TEAM = 0;
    private GameTeamColors DEFAULT_TEAM = GameTeamColors.RED;

    public WiredEffectJoinTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectJoinTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
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
            WiredGame game = (WiredGame) room.getGameOrCreate(WiredGame.class);

            if (habbo.getHabboInfo().getGamePlayer() != null && habbo.getHabboInfo().getCurrentGame() != null && (habbo.getHabboInfo().getCurrentGame() != WiredGame.class || (habbo.getHabboInfo().getCurrentGame() == WiredGame.class && habbo.getHabboInfo().getGamePlayer().getTeamColor() != teamColor))) {
                Game currentGame = room.getGame(habbo.getHabboInfo().getCurrentGame());
                currentGame.removeHabbo(habbo);
            }

            if(habbo.getHabboInfo().getGamePlayer() == null) {
                game.addHabbo(habbo, teamColor);
            }

            return true;
        }

        return false;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(DEFAULT_TEAM.type);
        }
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.JOIN_TEAM;
    }
}
