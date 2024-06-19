package com.eu.habbo.roleplay.government;

import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpTag;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Getter
    private LicenseManager licenseManager;

    private GovernmentManager() {
        long millis = System.currentTimeMillis();
        this.licenseManager = LicenseManager.getInstance();

        this.farmingCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.FARMING_AUTHORITY).get(0);
        if (this.farmingCorp == null) {
            throw new RuntimeException("GovernmentManager expected a state farming corp to exist.  Please create one in rp_corporations");
        }

        this.fishingCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.FISHING_AUTHORITY).get(0);
        if (this.fishingCorp == null) {
            throw new RuntimeException("GovernmentManager expected a fishing corp to exist.  Please create one in rp_corporations");
        }

        this.miningCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.MINING_AUTHORITY).get(0);
        if (this.miningCorp == null) {
            throw new RuntimeException("GovernmentManager expected a mining corp to exist.  Please create one in rp_corporations");
        }

        this.policeCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.POLICE).get(0);
        if (this.policeCorp == null) {
            throw new RuntimeException("GovernmentManager expected a police corp to exist.  Please create one in rp_corporations");
        }

        this.weaponsCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.WEAPONS_AUTHORITY).get(0);
        if (this.weaponsCorp == null) {
            throw new RuntimeException("GovernmentManager expected a weapons corp to exist.  Please create one in rp_corporations");
        }

        this.welfareCorp = CorpManager.getInstance().getCorpsByTag(CorpTag.WELFARE).get(0);
        if (this.welfareCorp == null) {
            throw new RuntimeException("GovernmentManager expected a welfare corp to exist.  Please create one in rp_corporations");
        }

        LOGGER.info("Government Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void dispose() {
        this.licenseManager.dispose();
    }
}
