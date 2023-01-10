package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserCreditsEvent extends UserEvent {

    private final int credits;


    public UserCreditsEvent(Habbo habbo, int credits) {
        super(habbo);

        this.credits = credits;
    }
}