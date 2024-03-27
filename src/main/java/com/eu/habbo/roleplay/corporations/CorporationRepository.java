package com.eu.habbo.roleplay.corporations;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CorporationRepository {

    private static CorporationRepository instance;

    public static CorporationRepository getInstance() {
        if (instance == null) {
            instance = new CorporationRepository();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorporationRepository.class);

    public TIntObjectHashMap<Corporation> getAllCorporations() {
        TIntObjectHashMap<Corporation> corps = new TIntObjectHashMap<>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_corporations ORDER BY id ASC")) {
            while (set.next()) {
                corps.put(set.getInt("id"), new Corporation(set));
            }
            return corps;
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
            return null;
        }
    }
}
