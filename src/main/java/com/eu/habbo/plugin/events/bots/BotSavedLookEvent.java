package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.HabboGender;
import lombok.Getter;

@Getter
public class BotSavedLookEvent extends BotEvent {

    private final HabboGender gender;


    private final String newLook;


    private final int effect;


    public BotSavedLookEvent(Bot bot, HabboGender gender, String newLook, int effect) {
        super(bot);

        this.gender = gender;
        this.newLook = newLook;
        this.effect = effect;
    }
}
