package com.eu.habbo.roleplay.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.corp.CorpPosition;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CorpPositionRepository {


    private static CorpPositionRepository instance;

    public static CorpPositionRepository getInstance() {
        if (instance == null) {
            instance = new CorpPositionRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpPositionRepository.class);

    public TIntObjectHashMap<CorpPosition> getAllCorporationPositions(int corporationId) {
        TIntObjectHashMap<CorpPosition> corpPositions = new TIntObjectHashMap<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_corporations_positions WHERE guild_id = ? ORDER BY id ASC")) {
            statement.setInt(1, corporationId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                corpPositions.put(set.getInt("id"), new CorpPosition(set));
            }
            return corpPositions;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }

    public CorpPosition getCorpPosition(int guildID, int orderID) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_corporations_positions WHERE guild_id = ? AND order_id = ? ORDER BY guild_id ASC LIMIT 1")) {
            statement.setInt(1, guildID);
            statement.setInt(2, orderID);

            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    return new CorpPosition(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }

    public void upsertCorpPosition(CorpPosition position) {
        this.upsertCorpPosition(
                position.getCorporationID(),
                position.getOrderID(),
                position.getName(),
                position.getDescription(),
                position.getSalary(),
                position.getMaleFigure(),
                position.getFemaleFigure(),
                position.isCanHire(),
                position.isCanFire(),
                position.isCanPromote(),
                position.isCanDemote(),
                position.isCanWorkAnywhere()
        );
    }

    public void upsertCorpPosition(int corpID, int orderID, String name, String description, int salary, String maleFigure, String femaleFigure, boolean canHire, boolean canFire, boolean canPromote, boolean canDemote, boolean canWorkAnywhere) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_corporations_positions (guild_id, order_id, name, description, salary, male_figure, female_figure, can_hire, can_fire, can_promote, can_demote, can_work_anywhere) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE guild_id = VALUES(guild_id), order_id = VALUES(order_id), name = VALUES(name), description = VALUES(description), salary = VALUES(salary), male_figure = VALUES(male_figure), female_figure = VALUES(female_figure), can_hire = VALUES(can_hire), can_fire = VALUES(can_fire), can_promote = VALUES(can_promote), can_demote = VALUES(can_demote), can_work_anywhere = VALUES(can_work_anywhere)")) {
                statement.setInt(1,corpID);
                statement.setInt(2, orderID);
                statement.setString(3, name);
                statement.setString(4, description);
                statement.setInt(5, salary);
                statement.setString(6, maleFigure);
                statement.setString(7, femaleFigure);
                statement.setInt(8, canHire ? 1 : 0);
                statement.setInt(9, canFire ? 1 : 0);
                statement.setInt(10, canPromote ? 1 : 0);
                statement.setInt(11, canDemote ? 1 : 0);
                statement.setInt(12, canWorkAnywhere ? 1 : 0);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}
