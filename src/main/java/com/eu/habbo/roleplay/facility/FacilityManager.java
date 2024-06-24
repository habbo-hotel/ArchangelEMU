package com.eu.habbo.roleplay.facility;

import com.eu.habbo.roleplay.facility.corp.FacilityCorpManager;
import com.eu.habbo.roleplay.facility.hospital.FacilityHospitalManager;
import com.eu.habbo.roleplay.facility.prison.FacilityPrisonManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class FacilityManager {

    private static FacilityManager instance;

    public static FacilityManager getInstance() {
        if (instance == null) {
            instance = new FacilityManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FacilityPrisonManager.class);

    private FacilityManager() {
        long millis = System.currentTimeMillis();
        LOGGER.info("Facility Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }


    public void cycle() {
        FacilityHospitalManager.getInstance().cycle();
        FacilityPrisonManager.getInstance().cycle();
        FacilityCorpManager.getInstance().cycle();
    }
}
