package com.eu.habbo.roleplay.corp;

import lombok.Getter;

@Getter
public enum CorpTag {
    BANK("bank"),
    CIVILIAN("civilian"),
    HOSPITAL("gov_hospital"),
    DRIVING_AUTHORITY("gov_driver"),
    WELFARE("gov_welfare"),
    POLICE("gov_police"),
    WEAPONS_AUTHORITY("gov_weapons"),
    FARMING_AUTHORITY("gov_farming"),
    FISHING_AUTHORITY("gov_fishing"),
    MINING_AUTHORITY("gov_mining");

    private final String value;

    CorpTag(String value) {
        this.value = value;
    }

    public static CorpTag fromValue(String value) {
        for (CorpTag type : CorpTag.values()) {
            if (value.equalsIgnoreCase(type.value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown corp tag value: " + value);
    }
}