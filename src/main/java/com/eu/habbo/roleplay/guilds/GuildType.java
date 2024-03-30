package com.eu.habbo.roleplay.guilds;

import lombok.Getter;

@Getter
public enum GuildType {
    Corp("corp"),
    Group("group"),
    Gang("gang");

    private final String type;

    GuildType(String type) {
        this.type = type;
    }

    public static GuildType fromString(String text) {
        for (GuildType guildType : GuildType.values()) {
            if (guildType.type.equalsIgnoreCase(text)) {
                return guildType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found in GuildType enum");
    }

}
