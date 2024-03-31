package com.eu.habbo.roleplay.corp;

import com.eu.habbo.roleplay.database.CorpRepository;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CorpManager {

    private static CorpManager instance;

    public static CorpManager getInstance() {
        if (instance == null) {
            instance = new CorpManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpManager.class);

    private TIntObjectHashMap<Corp> corporations;

    public Corp getCorpByID(int corporationID) {
        return this.corporations.get(corporationID);
    }

    public Corp getCorpsByName(String corpName) {
        int[] keys = this.corporations.keys();
        for (int key : keys) {
            Corp corp = this.corporations.get(key);
            if (corp.getGuild().getName().equalsIgnoreCase(corpName)) {
                return corp;
            }
        }
        return null;
    }

    public List<Corp> getCorpsByTag(String tag) {
        List<Corp> corporationsWithTag = new ArrayList<>();
        int[] keys = corporations.keys();
        for (int key : keys) {
            Corp corp = corporations.get(key);
            List<String> tags = corp.getTags();
            if (tags != null && tags.contains(tag)) {
                corporationsWithTag.add(corp);
            }
        }

        return corporationsWithTag;
    }

    @Getter
    @Setter
    private CorpShiftManager corpShiftManager;


    private CorpManager() {
        long millis = System.currentTimeMillis();
        this.corporations = CorpRepository.getInstance().getAllCorps();
        this.corpShiftManager = CorpShiftManager.getInstance();

        this.reload();

        LOGGER.info("Corp Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.corporations = CorpRepository.getInstance().getAllCorps();
    }

    public void dispose() {
        this.corporations = null;
        CorpManager.LOGGER.info("Corp Manager -> Disposed!");
    }
}