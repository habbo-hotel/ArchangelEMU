package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;

public class BattleBanzaiGameTeam extends GameTeam {
    public BattleBanzaiGameTeam(GameTeamColors teamColor) {
        super(teamColor);
    }

    @Override
    public void addMember(GamePlayer gamePlayer) {
        super.addMember(gamePlayer);
        RoomHabbo roomHabbo = gamePlayer.getHabbo().getRoomUnit();
        if (roomHabbo.getEffectId() > 0) {
            roomHabbo.setPreviousEffectId(roomHabbo.getEffectId());
            roomHabbo.setPreviousEffectEndTimestamp(roomHabbo.getPreviousEffectEndTimestamp());
        }

        gamePlayer.getHabbo().getRoomUnit().giveEffect(BattleBanzaiGame.effectId + this.teamColor.type, -1, true);
    }

    @Override
    public void removeMember(GamePlayer gamePlayer) {
        super.removeMember(gamePlayer);
        if (gamePlayer == null || gamePlayer.getHabbo() == null || gamePlayer.getHabbo().getRoomUnit().getRoom() == null)
            return;

        Habbo habbo = gamePlayer.getHabbo();
        Game game = habbo.getRoomUnit().getRoom().getGame(FreezeGame.class);
        RoomHabbo roomHabbo = habbo.getRoomUnit();
        Room room = roomHabbo.getRoom();
        if(room == null) return;
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
            habbo.getRoomUnit().giveEffect(nextEffectM, nextEffectDuration, true);
            return;
        }

        if (habbo.getHabboInfo().getGender().equals(HabboGender.F)) {
            habbo.getRoomUnit().giveEffect(nextEffectF, nextEffectDuration, true);
        }

        roomHabbo.setCanWalk(true);


        if (room.getRoomSpecialTypes() != null) {
            for (InteractionGameGate gate : room.getRoomSpecialTypes().getBattleBanzaiGates().values()) {
                gate.updateState(game, 5);
            }
        }
    }
}
