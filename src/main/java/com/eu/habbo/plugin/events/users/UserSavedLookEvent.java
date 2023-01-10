package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import lombok.Getter;

@Getter
public class UserSavedLookEvent extends UserEvent {
    private final HabboGender gender;
    private final String newLook;


    public UserSavedLookEvent(Habbo habbo, HabboGender gender, String newLook) {
        super(habbo);
        this.gender = gender;
        this.newLook = newLook;
    }
}
