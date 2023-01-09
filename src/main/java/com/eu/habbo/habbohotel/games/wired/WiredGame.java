package com.eu.habbo.habbohotel.games.wired;

import com.eu.habbo.habbohotel.games.*;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;

public class WiredGame extends Game {
    public GameState state = GameState.RUNNING;

    public WiredGame(Room room) {
        super(GameTeam.class, GamePlayer.class, room, false);
    }

    @Override
    public void initialise() {
        this.state = GameState.RUNNING;

        for (GameTeam team : this.teams.values()) {
            team.resetScores();
        }
    }

    @Override
    public void run() {
        this.state = GameState.RUNNING;
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        RoomUnit roomUnit = habbo.getRoomUnit();
        if (roomUnit.getEffectId() > 0)
            roomUnit.setPreviousEffectId(roomUnit.getEffectId(), roomUnit.getPreviousEffectEndTimestamp());
        this.room.giveEffect(habbo, FreezeGame.effectId + teamColor.type, -1, true);
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void removeHabbo(Habbo habbo) {
        super.removeHabbo(habbo);
        RoomUnit roomUnit = habbo.getRoomUnit();
        Room room = this.room;
        if (room == null) return;

        HabboItem topItem = room.getTopItemAt(roomUnit.getCurrentLocation().getX(), roomUnit.getCurrentLocation().getY());
        int nextEffectM = 0;
        int nextEffectF = 0;
        int nextEffectDuration = -1;

        if (topItem != null) {
            nextEffectM = topItem.getBaseItem().getEffectM();
            nextEffectF = topItem.getBaseItem().getEffectF();
        } else if (roomUnit.getPreviousEffectId() > 0) {
            nextEffectF = roomUnit.getPreviousEffectId();
            nextEffectM = roomUnit.getPreviousEffectId();
            nextEffectDuration = roomUnit.getPreviousEffectEndTimestamp();
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.M)) {
            room.giveEffect(habbo, nextEffectM, nextEffectDuration, true);
            return;
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.F)) {
            room.giveEffect(habbo, nextEffectF, nextEffectDuration, true);
        }
    }

    @Override
    public void stop() {
        this.state = GameState.RUNNING;
    }

    @Override
    public GameState getState() {
        return GameState.RUNNING;
    }
}