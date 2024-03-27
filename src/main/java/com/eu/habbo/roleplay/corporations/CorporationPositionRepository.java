package com.eu.habbo.roleplay.corporations;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class CorporationPositionRepository {


    private static CorporationPositionRepository instance;

    public static CorporationPositionRepository getInstance() {
        if (instance == null) {
            instance = new CorporationPositionRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorporationPositionRepository.class);

    public TIntObjectHashMap<CorporationPosition> getAllCorporationPositions() {
        TIntObjectHashMap<CorporationPosition> corpPositions = new TIntObjectHashMap<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_corporations_positions ORDER BY id ASC")) {
            while (set.next()) {
                corpPositions.put(set.getInt("id"), new CorporationPosition(set));
            }
            return corpPositions;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public void upsertCorporationPosition(CorporationPosition position) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_corporations_positions (corporation_id, order_id, name, description, salary, male_figure, female_figure, can_hire, can_fire, can_promote, can_demote) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE corporation_id = VALUES(corporation_id), order_id = VALUES(order_id), name = VALUES(name), description = VALUES(description), salary = VALUES(salary), male_figure = VALUES(male_figure), female_figure = VALUES(female_figure), can_hire = VALUES(can_hire), can_fire = VALUES(can_fire), can_promote = VALUES(can_promote), can_demote = VALUES(can_demote)")) {
                statement.setInt(1, position.getCorporationID());
                statement.setInt(2, position.getOrderID());
                statement.setString(3, position.getName());
                statement.setString(4, position.getDescription());
                statement.setInt(5, position.getSalary());
                statement.setString(6, position.getMaleFigure());
                statement.setString(7, position.getFemaleFigure());
                statement.setInt(8, position.isCanFire() ? 1 : 0);
                statement.setInt(9, position.isCanFire() ? 1 : 0);
                statement.setInt(10, position.isCanPromote() ? 1 : 0);
                statement.setInt(11, position.isCanDemote() ? 1 : 0);
                statement.setInt(12, position.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
