package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import lombok.Getter;

@Getter
public class GuildChangedNameEvent extends GuildEvent {

    private final String name;


    private final String description;


    public GuildChangedNameEvent(Guild guild, String name, String description) {
        super(guild);
        this.name = name;
        this.description = description;
    }
}
