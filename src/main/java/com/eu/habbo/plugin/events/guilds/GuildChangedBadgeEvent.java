package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import lombok.Getter;

@Getter
public class GuildChangedBadgeEvent extends GuildEvent {

    private final String badge;


    public GuildChangedBadgeEvent(Guild guild, String badge) {
        super(guild);

        this.badge = badge;
    }
}
