package com.eu.habbo.habbohotel.games.wired;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

public class WiredGame extends Game {
    public WiredGame(Room room) {
        super(GameTeam.class, GamePlayer.class, room, false);
    }

    @Override
    public void initialise() {

    }

    @Override
    public void run() {

    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        this.room.giveEffect(habbo, FreezeGame.effectId + teamColor.type, -1);
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void removeHabbo(Habbo habbo) {
        super.removeHabbo(habbo);
        this.room.giveEffect(habbo, 0, -1);
    }
}