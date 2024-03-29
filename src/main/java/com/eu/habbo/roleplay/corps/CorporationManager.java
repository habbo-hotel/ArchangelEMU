package com.eu.habbo.roleplay.corps;

import com.eu.habbo.roleplay.database.CorporationRepository;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public Corporation getCorporationByName(String corpName) {
        int[] keys = this.corporations.keys();
        for (int key : keys) {
            Corporation corp = this.corporations.get(key);
            if (corp.getName().equalsIgnoreCase(corpName)) {
                return corp;
            }
        }
        return null;
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
        this.corporations = CorporationRepository.getInstance().getAllCorporations();
        this.corporationsShiftManager = CorporationsShiftManager.getInstance();

        this.reload();

        LOGGER.info("Corporations Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.corporations = CorporationRepository.getInstance().getAllCorporations();
    }

    public void dispose() {
        this.corporations = null;
        CorporationManager.LOGGER.info("Corporation Manager -> Disposed!");
    }
}