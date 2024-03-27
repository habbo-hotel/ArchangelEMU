package com.eu.habbo.roleplay.corporations;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.database.CorporationPositionRepository;
import com.eu.habbo.roleplay.database.CorporationRepository;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

@Getter
public class Corporation  implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorporationPosition.class);
    private int id;
    private int userID;
    @Setter
    private int roomID;
    @Setter
    private String name;
    @Setter
    private String description;
    private TIntObjectHashMap<CorporationPosition> positions;

    public CorporationPosition getPositionByOrderID(int orderID) {
        int[] keys = positions.keys();
        for (int key : keys) {
            CorporationPosition position = positions.get(key);
            if (position.getOrderID() == orderID) {
                return position;
            }
        }
        return null;
    }
    private TIntObjectHashMap<Habbo> invitedUsers;

    public void addInvitedUser(Habbo habbo) {
        this.invitedUsers.put(habbo.getHabboInfo().getId(), habbo);
    }

    public Habbo getInvitedUser(Habbo habbo) {
        return this.invitedUsers.get(habbo.getHabboInfo().getId());
    }

    public void removeInvitedUser(Habbo habbo) {
        this.invitedUsers.remove(habbo.getHabboInfo().getId());
    }

    @Getter
    private List<String> tags;

    public CorporationPosition getPositionByID(int positionID) {
        return this.positions.get(positionID);
    }

    public Corporation(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.userID = set.getInt("user_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.tags = Arrays.stream(set.getString("tags").split(";")).toList();
        this.positions = CorporationPositionRepository.getInstance().getAllCorporationPositions();
        this.invitedUsers = new TIntObjectHashMap<>();
    }

    @Override
    public void run() {
        CorporationRepository.getInstance().upsertCorporation(this);
        TIntObjectIterator<CorporationPosition> iterator = positions.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            CorporationPosition position = iterator.value();
            position.run();
        }
    }


}