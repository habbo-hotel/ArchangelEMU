package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;

public class AcceptTradingEvent extends TradingEvent {
    @Override
    public void handle() {
        Habbo habbo = this.client.getHabbo();

        if (habbo == null || habbo.getHabboInfo() == null || habbo.getRoomUnit().getRoom() == null)
            return;

        RoomTrade trade = getActiveRoomTrade(habbo);

        if (trade == null)
            return;

        trade.accept(habbo, true);
    }
}
