package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ConfirmAcceptTradingEvent extends MessageHandler {
    @Override
    public void handle() {
        Habbo habbo = this.client.getHabbo();
        RoomTrade trade = habbo.getHabboInfo().getCurrentRoom().getActiveTradeForHabbo(habbo);

        if (trade == null || !trade.getRoomTradeUserForHabbo(habbo).isAccepted())
            return;

        trade.confirm(habbo);
    }
}
