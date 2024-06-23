package com.eu.habbo.roleplay.government;

import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpTag;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Getter
public class GovernmentManager {

    private static GovernmentManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(GovernmentManager.class);

    public static GovernmentManager getInstance() {
        if (instance == null) {
            instance = new GovernmentManager();
        }
        return instance;
    }
    private final Corp fishingCorp;
    private final Corp miningCorp;
    private final Corp farmingCorp;
    private final Corp policeCorp;
    private final Corp welfareCorp;
    private final Corp weaponsCorp;

    private GovernmentManager() {
        long millis = System.currentTimeMillis();

        List<Corp> farmingCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.FARMING_AUTHORITY);
        if (farmingCorps.isEmpty()) {
            GovernmentManager.LOGGER.warn("GovernmentManager expected a state farming corp to exist.  Please create one in rp_corporations");
            this.farmingCorp = null;
        } else {
            this.farmingCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.FARMING_AUTHORITY).get(0);
        }

        List<Corp> fishingCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.FISHING_AUTHORITY);
        if (fishingCorps.isEmpty()) {
            GovernmentManager.LOGGER.warn("GovernmentManager expected a fishing corp to exist.  Please create one in rp_corporations");
            this.fishingCorp = null;
        } else {
            this.fishingCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.FISHING_AUTHORITY).get(0);
        }

        List<Corp> miningCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.MINING_AUTHORITY);
        if (miningCorps.isEmpty()) {
            GovernmentManager.LOGGER.warn("GovernmentManager expected a mining corp to exist.  Please create one in rp_corporations");
            this.miningCorp = null;
        } else {
            this.miningCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.MINING_AUTHORITY).get(0);
        }

        List<Corp> policeCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.POLICE);
        if (policeCorps.isEmpty()) {
            GovernmentManager.LOGGER.warn("GovernmentManager expected a police corp to exist.  Please create one in rp_corporations");
            this.policeCorp = null;
        } else {
            this.policeCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.POLICE).get(0);
        }

        List<Corp> weaponsCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.WEAPONS_AUTHORITY);
        if (weaponsCorps.isEmpty()) {
            GovernmentManager.LOGGER.warn("GovernmentManager expected a weapons corp to exist.  Please create one in rp_corporations");
            this.weaponsCorp = null;
        } else {
            this.weaponsCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.WEAPONS_AUTHORITY).get(0);
        }

        List<Corp> welfareCorps = CorpManager.getInstance().getCorpsByTag(CorpTag.WELFARE);
        if (welfareCorps.isEmpty()) {
            GovernmentManager.LOGGER.warn("GovernmentManager expected a welfare corp to exist.  Please create one in rp_corporations");
            this.welfareCorp = null;
        } else {
            this.welfareCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.WELFARE).get(0);
        }

        LOGGER.info("Government Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
}
