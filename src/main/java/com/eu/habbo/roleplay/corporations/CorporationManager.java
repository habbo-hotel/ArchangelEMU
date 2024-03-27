package com.eu.habbo.roleplay.corporations;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CorporationManager {

    private static CorporationManager instance;

    public static CorporationManager getInstance() {
        if (instance == null) {
            instance = new CorporationManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorporationManager.class);

    private TIntObjectHashMap<Corporation> corporations;

    public Corporation getCorporationByID(int corporationID) {
        return this.corporations.get(corporationID);
    }

    public List<Corporation> getCorporationsWithTag(String tag) {
        List<Corporation> corporationsWithTag = new ArrayList<>();
        int[] keys = corporations.keys();
        for (int key : keys) {
            Corporation corporation = corporations.get(key);
            List<String> tags = corporation.getTags();
            if (tags != null && tags.contains(tag)) {
                corporationsWithTag.add(corporation);
            }
        }

        return corporationsWithTag;
    }

    @Getter
    @Setter
    private CorporationsShiftManager corporationsShiftManager;


    private CorporationManager() {
        long millis = System.currentTimeMillis();
        this.corporations = new TIntObjectHashMap<>();
        this.corporationsShiftManager = CorporationsShiftManager.getInstance();

        this.reload();

        LOGGER.info("Corporations Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.loadCorporations();
    }

    private void loadCorporations() {
        this.corporations.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_corporations ORDER BY id ASC")) {
            while (set.next()) {
                Corporation corporation = null;
                if (!this.corporations.containsKey(set.getInt("id"))) {
                    corporation = new Corporation(set);
                    this.corporations.put(set.getInt("id"), corporation);
                } else {
                    corporation = this.corporations.get(set.getInt("id"));
                    corporation.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.corporations = null;
    }
}