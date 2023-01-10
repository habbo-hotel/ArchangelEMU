package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class SupportUserAlertedEvent extends SupportEvent {

    private final Habbo target;

    private final String message;

    private final SupportUserAlertedReason reason;

    public SupportUserAlertedEvent(Habbo moderator, Habbo target, String message, SupportUserAlertedReason reason) {
        super(moderator);

        this.message = message;
        this.target = target;
        this.reason = reason;
    }
}