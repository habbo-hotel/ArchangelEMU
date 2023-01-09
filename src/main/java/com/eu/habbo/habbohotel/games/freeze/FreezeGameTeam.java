package com.eu.habbo.habbohotel.games.freeze;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FreezeGameTeam extends GameTeam {
    public FreezeGameTeam(GameTeamColors teamColor) {
        super(teamColor);
    }

    @Override
    public void removeMember(GamePlayer gamePlayer) {
        super.removeMember(gamePlayer);
        if (gamePlayer == null || gamePlayer.getHabbo() == null || gamePlayer.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        Habbo habbo = gamePlayer.getHabbo();
        Game game = habbo.getHabboInfo().getCurrentRoom().getGame(FreezeGame.class);
        RoomUnit roomUnit = habbo.getRoomUnit();
        Room room = roomUnit.getRoom();
        if(room == null) return;

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

        roomUnit.setCanWalk(true);

        if (room.getRoomSpecialTypes() != null) {
            for (InteractionGameGate gate : room.getRoomSpecialTypes().getFreezeGates().values()) {
                gate.updateState(game, 5);
            }
        }
    }

    @Override
    public void addMember(GamePlayer gamePlayer) {
        super.addMember(gamePlayer);

        RoomUnit roomUnit = gamePlayer.getHabbo().getRoomUnit();
        if (roomUnit.getEffectId() > 0)
            roomUnit.setPreviousEffectId(roomUnit.getEffectId(), roomUnit.getPreviousEffectEndTimestamp());
        gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gamePlayer.getHabbo(), FreezeGame.effectId + this.teamColor.type, -1, true);
    }
}
