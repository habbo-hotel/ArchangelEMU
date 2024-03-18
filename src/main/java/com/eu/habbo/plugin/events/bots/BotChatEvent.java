package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import lombok.Getter;

@Getter
public abstract class BotChatEvent extends BotEvent {

    private final String message;


    public BotChatEvent(Bot bot, String message) {
        super(bot);

        this.message = message;
    }
}
