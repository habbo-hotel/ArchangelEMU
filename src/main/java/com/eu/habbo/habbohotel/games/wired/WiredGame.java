package com.eu.habbo.habbohotel.games.wired;

import com.eu.habbo.habbohotel.games.*;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;

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
        RoomHabbo roomHabbo = habbo.getRoomUnit();
        if (roomHabbo.getEffectId() > 0)
            roomHabbo.setPreviousEffectId(roomHabbo.getEffectId(), roomHabbo.getPreviousEffectEndTimestamp());
        roomHabbo.giveEffect(FreezeGame.effectId + teamColor.type, -1, true);
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void removeHabbo(Habbo habbo) {
        super.removeHabbo(habbo);
        RoomHabbo roomHabbo = habbo.getRoomUnit();
        Room room = this.room;
        if (room == null) return;

        RoomItem topItem = room.getRoomItemManager().getTopItemAt(roomHabbo.getCurrentPosition().getX(), roomHabbo.getCurrentPosition().getY());
        int nextEffectM = 0;
        int nextEffectF = 0;
        int nextEffectDuration = -1;

        if (topItem != null) {
            nextEffectM = topItem.getBaseItem().getEffectM();
            nextEffectF = topItem.getBaseItem().getEffectF();
        } else if (roomHabbo.getPreviousEffectId() > 0) {
            nextEffectF = roomHabbo.getPreviousEffectId();
            nextEffectM = roomHabbo.getPreviousEffectId();
            nextEffectDuration = roomHabbo.getPreviousEffectEndTimestamp();
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.M)) {
            roomHabbo.giveEffect(nextEffectM, nextEffectDuration, true);
            return;
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.F)) {
            roomHabbo.giveEffect(nextEffectF, nextEffectDuration, true);
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