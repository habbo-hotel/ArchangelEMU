package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.roleplay.guilds.GuildType;
import lombok.Getter;

@Getter
public class GuildChangedNameEvent extends GuildEvent {

    private final String name;

    private final GuildType type;


    private final String description;


    public GuildChangedNameEvent(Guild guild, GuildType type, String name, String description) {
        super(guild);
        this.type = type;
        this.name = name;
        this.description = description;
    }
}
