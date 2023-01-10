package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserSavedMottoEvent extends UserEvent {
    private final String oldMotto;
    private final String newMotto;


    public UserSavedMottoEvent(Habbo habbo, String oldMotto, String newMotto) {
        super(habbo);
        this.oldMotto = oldMotto;
        this.newMotto = newMotto;
    }
}
