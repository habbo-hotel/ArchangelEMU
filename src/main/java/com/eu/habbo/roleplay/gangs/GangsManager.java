package com.eu.habbo.roleplay.gangs;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.corporations.Corporation;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GangsManager {

    private static GangsManager instance;

    public static GangsManager getInstance() {
        if (instance == null) {
            instance = new GangsManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GangsManager.class);

    private TIntObjectHashMap<Gang> gangs;

    public Gang getGangById(int gangID) {
        return this.gangs.get(gangID);
    }

    public Gang getGangByName(String gangName) {
        int[] keys = this.gangs.keys();
        for (int key : keys) {
            Gang gang = this.gangs.get(key);
            if (gang.getName().equalsIgnoreCase(gangName)) {
                return gang;
            }
        }
        return null;
    }


    private GangsManager() {
        long millis = System.currentTimeMillis();
        this.gangs = new TIntObjectHashMap<>();

        this.reload();

        LOGGER.info("Gangs Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.loadGangs();
    }

    private void loadGangs() {
        this.gangs.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_gangs ORDER BY id ASC")) {
            while (set.next()) {
                Gang gang = null;
                if (!this.gangs.containsKey(set.getInt("id"))) {
                    gang = new Gang(set);
                    this.gangs.put(set.getInt("id"), gang);
                } else {
                    gang = this.gangs.get(set.getInt("id"));
                    gang.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.gangs = null;
    }
}