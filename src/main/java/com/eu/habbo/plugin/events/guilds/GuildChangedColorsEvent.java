package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import lombok.Getter;

@Getter
public class GuildChangedColorsEvent extends GuildEvent {

    private final int colorOne;


    private final int colorTwo;


    public GuildChangedColorsEvent(Guild guild, int colorOne, int colorTwo) {
        super(guild);

        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
    }
}
