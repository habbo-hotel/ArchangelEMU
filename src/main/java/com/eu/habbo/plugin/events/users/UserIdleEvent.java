package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserIdleEvent extends UserEvent {
    private final IdleReason reason;
    private final boolean idle;
    public UserIdleEvent(Habbo habbo, IdleReason reason, boolean idle) {
        super(habbo);

        this.reason = reason;
        this.idle = idle;
    }


    public enum IdleReason {
        ACTION,
        DANCE,
        TIMEOUT,
        WALKED,
        TALKED
    }
}