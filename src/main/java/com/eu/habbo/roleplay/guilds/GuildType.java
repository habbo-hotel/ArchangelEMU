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

}
