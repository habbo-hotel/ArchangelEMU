package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.RoomItemManager;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionGameScoreboard extends InteractionGameTeamItem {
    protected InteractionGameScoreboard(ResultSet set, Item baseItem, GameTeamColors teamColor) throws SQLException {
        super(set, baseItem, teamColor);
        this.setExtraData("0");
    }

    protected InteractionGameScoreboard(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells, GameTeamColors teamColor) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells, teamColor);
        this.setExtraData("0");
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");
    }

    @Override
    public void removeThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getGameScoreboards().remove(getId());
    }
    @Override
    public void addThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getGameScoreboards().put(getId(), this);
    }
}
