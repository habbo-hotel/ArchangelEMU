package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.Getter;

@Getter
public class FurnitureDiceRolledEvent extends FurnitureUserEvent {

    private final int result;


    public FurnitureDiceRolledEvent(HabboItem furniture, Habbo habbo, int result) {
        super(furniture, habbo);

        this.result = result;
    }
}
