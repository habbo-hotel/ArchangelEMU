package com.eu.habbo.roleplay.government;

import com.eu.habbo.roleplay.corporations.Corporation;
import com.eu.habbo.roleplay.corporations.CorporationManager;
import lombok.Getter;

@Getter
public class GovernmentManager {

    public static String WELFARE_CORP_TAG = "welfare";

    private static GovernmentManager instance;

    public static GovernmentManager getInstance() {
        if (instance == null) {
            instance = new GovernmentManager();
        }
        return instance;
    }

    private Corporation welfareCorp;

    private GovernmentManager() {
        this.welfareCorp = CorporationManager.getInstance().getCorporationsWithTag(GovernmentManager.WELFARE_CORP_TAG).get(0);
        if (this.welfareCorp == null) {
           throw new RuntimeException("GovernmentManager expected a welfare corp to exist.  Please create one in rp_corporations");
        }
    }
}
