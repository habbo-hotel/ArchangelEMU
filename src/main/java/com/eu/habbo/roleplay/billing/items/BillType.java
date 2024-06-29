package com.eu.habbo.roleplay.billing.items;

import lombok.Getter;

@Getter
public enum BillType {
    DRIVER_LICENSE("driver-license"),
    FARMING_LICENSE("farming-license"),
    FISHING_LICENSE("fishing-license"),
    MINING_LICENSE("mining-license"),
    WEAPON_LICENSE("weapon-license"),
    LUMBERJACK_LICENSE("lumberjack-license");

    private final String value;

    BillType(String value) {
        this.value = value;
    }

    public static BillType fromValue(String value) {
        for (BillType type : BillType.values()) {
            if (value.equalsIgnoreCase(type.value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown bill type value: " + value);
    }
}