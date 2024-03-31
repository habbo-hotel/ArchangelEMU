package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import lombok.Getter;

@Getter
public class GuildChangedSettingsEvent extends GuildEvent {

    private final int state;

    private final boolean rights;

    public GuildChangedSettingsEvent(Guild guild, int state, boolean rights) {
        super(guild);
        this.state = state;
        this.rights = rights;
    }
}
