package com.eu.habbo.roleplay.government;

import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class GovernmentManager {

    public static String WELFARE_CORP_TAG = "welfare";

    private static GovernmentManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(GovernmentManager.class);

    public static GovernmentManager getInstance() {
        if (instance == null) {
            instance = new GovernmentManager();
        }
        return instance;
    }

    private Corp welfareCorp;

    private GovernmentManager() {
        long millis = System.currentTimeMillis();
        this.welfareCorp = CorpManager.getInstance().getCorpsByTag(GovernmentManager.WELFARE_CORP_TAG).get(0);
        if (this.welfareCorp == null) {
           throw new RuntimeException("GovernmentManager expected a welfare corp to exist.  Please create one in rp_corporations");
        }
        LOGGER.info("Government Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
}
