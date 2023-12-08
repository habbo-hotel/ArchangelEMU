package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;

public class FurnitureRedeemedEvent extends FurnitureUserEvent {
    public static final int CREDITS = -1;
    public static final int PIXELS = 0;
    public static final int DIAMONDS = 5;

    public final int amount;
    public final int currencyID;

    public FurnitureRedeemedEvent(RoomItem furniture, Habbo habbo, int amount, int currencyID) {
        super(furniture, habbo);

        this.amount = amount;
        this.currencyID = currencyID;
    }
}
