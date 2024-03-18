package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class BotWhisperEvent extends BotChatEvent {

    private final Habbo target;


    public BotWhisperEvent(Bot bot, String message, Habbo target) {
        super(bot, message);

        this.target = target;
    }
}
