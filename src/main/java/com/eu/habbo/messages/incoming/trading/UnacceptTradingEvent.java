package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;

public class UnacceptTradingEvent extends TradingEvent {

    @Override
    public void handle() {
        Habbo habbo = this.client.getHabbo();
        RoomTrade trade = getActiveRoomTrade(habbo);

        if (trade == null)
            return;

        trade.accept(habbo, false);
    }
}
