package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;

public class ConfirmAcceptTradingEvent extends TradingEvent {
    @Override
    public void handle() {
        Habbo habbo = this.client.getHabbo();
        RoomTrade trade = getActiveRoomTrade(habbo);

        if (trade == null || !trade.getRoomTradeUserForHabbo(habbo).isAccepted())
            return;

        trade.confirm(habbo);
    }
}
