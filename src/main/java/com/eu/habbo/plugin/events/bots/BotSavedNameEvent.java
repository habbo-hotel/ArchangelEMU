package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import lombok.Getter;

@Getter
public class BotSavedNameEvent extends BotEvent {

    private final String name;


    public BotSavedNameEvent(Bot bot, String name) {
        super(bot);

        this.name = name;
    }
}
