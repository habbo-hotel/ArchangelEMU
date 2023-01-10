package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.Getter;

@Getter
public class FurnitureToggleEvent extends FurnitureUserEvent {
    private final int state;

    public FurnitureToggleEvent(HabboItem furniture, Habbo habbo, int state) {
        super(furniture, habbo);

        this.state = state;
    }
}
