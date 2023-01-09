package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModToolBanType {
    ACCOUNT("account"),
    MACHINE("machine"),
    SUPER("super"),
    IP("ip"),
    UNKNOWN("???");

    private final String type;

    public static ModToolBanType fromString(String type) {
        for (ModToolBanType t : ModToolBanType.values()) {
            if (t.type.equalsIgnoreCase(type)) {
                return t;
            }
        }

        return UNKNOWN;
    }
}