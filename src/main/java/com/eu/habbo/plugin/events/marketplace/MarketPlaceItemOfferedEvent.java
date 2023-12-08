package com.eu.habbo.plugin.events.marketplace;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import lombok.Getter;

@Getter
public class MarketPlaceItemOfferedEvent extends MarketPlaceEvent {
    private final Habbo habbo;
    private final RoomItem item;
    private final int price;

    public MarketPlaceItemOfferedEvent(Habbo habbo, RoomItem item, int price) {
        this.habbo = habbo;
        this.item = item;
        this.price = price;
    }
}
