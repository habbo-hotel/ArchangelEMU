package com.eu.habbo.roleplay.tonics;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TonicsManager {
    private static TonicsManager instance;

    public static TonicsManager getInstance() {
        if (instance == null) {
            instance = new TonicsManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TonicsManager.class);

    private TIntObjectHashMap<Tonic> tonics;

    public Tonic getTonicByID(int tonicID) {
        return this.tonics.get(tonicID);
    }


    private TonicsManager() {
        long millis = System.currentTimeMillis();
        this.tonics = new TIntObjectHashMap<>();

        this.reload();

        LOGGER.info("Tonics Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reload() {
        this.loadTonics();
    }

    private void loadTonics() {
        this.tonics.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_tonics ORDER BY id ASC")) {
            while (set.next()) {
                Tonic tonic = null;
                if (!this.tonics.containsKey(set.getInt("id"))) {
                    tonic = new Tonic(set);
                    this.tonics.put(set.getInt("id"), tonic);
                } else {
                    tonic = this.tonics.get(set.getInt("id"));
                    tonic.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.tonics = null;
    }
}