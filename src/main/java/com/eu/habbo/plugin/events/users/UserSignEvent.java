package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserSignEvent extends UserEvent {
    private final int sign;

    public UserSignEvent(Habbo habbo, int sign) {
        super(habbo);
        this.sign = sign;
    }
}
