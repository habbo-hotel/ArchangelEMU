package com.eu.habbo.roleplay.government;

import lombok.Getter;

@Getter
public enum LicenseType {
    DRIVER(1),
    FARMING(2),
    FISHING(3),
    MINING(4),
    WEAPON(5),
    LUMBERJACK(6);

    private final int value;

    LicenseType(int value) {
        this.value = value;
    }

    public static LicenseType fromValue(int value) {
        for (LicenseType type : LicenseType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown license type value: " + value);
    }
}