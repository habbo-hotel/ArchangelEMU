package com.eu.habbo.roleplay.government;

public enum LicenseType {
    DRIVER(1),
    WEAPON(2),
    FARMING(3),
    FISHING(4),
    MINING(5);

    private final int value;

    LicenseType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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