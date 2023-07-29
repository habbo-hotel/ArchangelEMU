package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionGameGate extends InteractionGameTeamItem {
    public InteractionGameGate(ResultSet set, Item baseItem, GameTeamColors teamColor) throws SQLException {
        super(set, baseItem, teamColor);
        this.setExtraData("0");
    }

    public InteractionGameGate(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells, GameTeamColors teamColor) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells, teamColor);
        this.setExtraData("0");
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    public void updateState(Game game, int maxPlayers) {
        int memberCount = 0;

        if (game.getTeam(this.teamColor) != null) {
            memberCount = game.getTeam(this.teamColor).getMembers().size();
        }

        if (memberCount > maxPlayers) {
            memberCount = maxPlayers;
        }
        this.setExtraData(memberCount + "");
        game.getRoom().updateItem(this);
    }
}
