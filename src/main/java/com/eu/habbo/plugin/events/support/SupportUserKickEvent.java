package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class SupportUserKickEvent extends SupportEvent {

    private final Habbo target;
    private final String message;


    public SupportUserKickEvent(Habbo moderator, Habbo target, String message) {
        super(moderator);

        this.target = target;
        this.message = message;
    }
}