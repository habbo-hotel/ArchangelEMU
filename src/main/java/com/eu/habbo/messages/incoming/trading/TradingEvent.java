package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public abstract class TradingEvent extends MessageHandler {
    protected void stopTrade(Habbo habbo) {
        Room room = habbo.getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        RoomTrade trade = room.getActiveTradeForHabbo(habbo);

        if (trade == null)
            return;

        trade.stopTrade(habbo);
        room.stopTrade(trade);
    }
}
