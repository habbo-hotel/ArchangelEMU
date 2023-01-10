package com.eu.habbo.plugin.events.marketplace;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.Getter;

@Getter
public class MarketPlaceItemOfferedEvent extends MarketPlaceEvent {
    private final Habbo habbo;
    private final HabboItem item;
    private final int price;

    public MarketPlaceItemOfferedEvent(Habbo habbo, HabboItem item, int price) {
        this.habbo = habbo;
        this.item = item;
        this.price = price;
    }
}
