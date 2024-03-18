package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InteractionVoteCounter extends RoomItem {

    private boolean frozen;
    private int votes;
    private final List<Integer> votedUsers;

    public InteractionVoteCounter(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        if(!this.getExtraData().contains(",")) {
            this.setExtraData("1,0"); // frozen,votes
        }

        String[] bits = this.getExtraData().split(",");
        frozen = bits[0].equals("1");
        votes = Integer.parseInt(bits[1]);
        votedUsers = new ArrayList<>();
    }

    public InteractionVoteCounter(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);

        if(!extradata.contains(",")) {
            extradata = "1,0";
        }

        String[] bits = extradata.split(",");
        frozen = bits[0].equals("1");
        votes = Integer.parseInt(bits[1]);
        votedUsers = new ArrayList<>();
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0) + 3);
        serverMessage.appendString(this.frozen ? "0" : "1");
        serverMessage.appendInt(this.votes);
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    private void updateExtradata() {
        this.setExtraData((this.frozen ? "1" : "0") + "," + this.votes);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
        if (room == null || !((client != null && room.getRoomRightsManager().hasRights(client.getHabbo())) || (objects.length >= 2 && objects[1] instanceof WiredEffectType)))
            return;

        this.frozen = !this.frozen;

        if(!frozen) {
            this.votes = 0;
            this.votedUsers.clear();
        }

        updateExtradata();
        this.setSqlUpdateNeeded(true);
        room.updateItem(this);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    public void vote(Room room, int UserId, int vote) {
        if(frozen)
            return;

        if(votedUsers.contains(UserId))
            return;

        votedUsers.add(UserId);

        votes += vote;
        updateExtradata();
        this.setSqlUpdateNeeded(true);
        room.updateItem(this);
    }
}
