package com.eu.habbo.roleplay.weapons;

import lombok.Getter;

@Getter
public enum WeaponType {
    GUN("gun"),
    MELEE("melee");

    // Getter to retrieve the string value of the enum
    private final String typeName;

    // Constructor for WeaponType enum
    WeaponType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }

    public static WeaponType fromString(String typeName) {
        for (WeaponType type : WeaponType.values()) {
            if (type.typeName.equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with typeName: " + typeName);
    }

}
