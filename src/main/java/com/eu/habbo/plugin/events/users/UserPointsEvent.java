package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserPointsEvent extends UserEvent {
    private final int points;
    private final int type;


    public UserPointsEvent(Habbo habbo, int points, int type) {
        super(habbo);

        this.points = points;
        this.type = type;
    }
}