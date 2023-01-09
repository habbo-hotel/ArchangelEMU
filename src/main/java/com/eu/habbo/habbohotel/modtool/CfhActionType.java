package com.eu.habbo.habbohotel.modtool;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CfhActionType {
    MODS(0),
    AUTO_REPLY(1),
    AUTO_IGNORE(2),
    GUARDIANS(3);

    private final int type;


    public static CfhActionType get(String name) {
        return switch (name) {
            case "auto_reply" -> CfhActionType.AUTO_REPLY;
            case "auto_ignore" -> CfhActionType.AUTO_IGNORE;
            case "guardians" -> CfhActionType.GUARDIANS;
            default -> CfhActionType.MODS;
        };

    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}