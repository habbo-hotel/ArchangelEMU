package com.eu.habbo.roleplay.corporations;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static com.eu.habbo.database.DatabaseConstants.CAUGHT_SQL_EXCEPTION;

@Getter
public class Corporation {

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
        int[] keys = positions.keys();
        for (int key : keys) {
            CorporationPosition position = positions.get(key);
            if (position.getId() == positionID) {
                return position;
            }
        }
        return null;
    }

    public Corporation(ResultSet set) throws SQLException {
        this.load(set);
    }

    public void load(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.userID = set.getInt("user_id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.positions = new TIntObjectHashMap<>();
        this.tags = Arrays.stream(set.getString("tags").split(";")).toList();
        this.invitedUsers = new TIntObjectHashMap<>();
        this.loadPositions();
    }

    private void loadPositions() {
        this.positions.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_corporations_positions WHERE corporation_id = ?")) {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    if (!this.positions.containsKey(set.getInt("id")))
                        this.positions.put(set.getInt("id"), new CorporationPosition(set));
                }
            }
        } catch (SQLException e) {
            Corporation.LOGGER.error(CAUGHT_SQL_EXCEPTION, e);
        }
    }


}