package com.eu.habbo.roleplay.police;

import com.eu.habbo.habbohotel.users.Habbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PoliceReportManager {

    private static PoliceReportManager instance;

    public static PoliceReportManager getInstance() {
        if (instance == null) {
            instance = new PoliceReportManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PoliceReportManager.class);

    private List<PoliceReport> policeReports;

    public void addPoliceReport(PoliceReport policeReport) {
        this.policeReports.add(policeReport);
    }

    public List<PoliceReport> getPoliceReportsByReportingUser(Habbo habbo) {
        List<PoliceReport> matchingReports = new ArrayList<PoliceReport>();
        for (PoliceReport policeReport : this.policeReports) {
            if (policeReport.reportingUser.getHabboInfo().getId() == habbo.getHabboInfo().getId()) {
                matchingReports.add(policeReport);
            }
        }
        return matchingReports;
    }

    public List<PoliceReport> getPoliceReportsByRespondingOfficer(Habbo habbo) {
        List<PoliceReport> matchingReports = new ArrayList<PoliceReport>();
        for (PoliceReport policeReport : this.policeReports) {
            if (habbo == null) {
                if (policeReport.respondingOfficer == null) {
                    matchingReports.add(policeReport);
                }
                continue;
            }
            if (policeReport.respondingOfficer.getHabboInfo().getId() == habbo.getHabboInfo().getId()) {
                matchingReports.add(policeReport);
            }
        }
        return matchingReports;
    }

    public void removePoliceReport(PoliceReport policeReport) {
        this.policeReports.remove(policeReport);
    }

    private PoliceReportManager() {
        long millis = System.currentTimeMillis();
        this.policeReports = new ArrayList<PoliceReport>();
        LOGGER.info("Police Report Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void dispose() {
        this.policeReports = null;
        PoliceReportManager.LOGGER.info("Police Report Manager -> Disposed!");
    }
}