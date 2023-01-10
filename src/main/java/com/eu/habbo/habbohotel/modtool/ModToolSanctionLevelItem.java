package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModToolSanctionLevelItem {
    private final int sanctionLevel;
    private final String sanctionType;
    private final int sanctionHourLength;
    private final int sanctionProbationDays;

}
