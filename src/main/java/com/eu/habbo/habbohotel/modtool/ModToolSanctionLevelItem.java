package com.eu.habbo.habbohotel.modtool;

import lombok.Getter;

@Getter
public class ModToolSanctionLevelItem {
    private  int sanctionLevel;
    private  String sanctionType;
    private  int sanctionHourLength;
    private  int sanctionProbationDays;

    public ModToolSanctionLevelItem(int sanctionLevel, String sanctionType, int sanctionHourLength, int sanctionProbationDays) {
        this.sanctionLevel = sanctionLevel;
        this.sanctionType = sanctionType;
        this.sanctionHourLength = sanctionHourLength;
        this.sanctionProbationDays = sanctionProbationDays;
    }
}
