package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class BotServerItemEvent extends BotEvent {

    private final Habbo habbo;

    private final int itemId;


    public BotServerItemEvent(Bot bot, Habbo habbo, int itemId) {
        super(bot);

        this.habbo = habbo;
        this.itemId = itemId;
    }
}