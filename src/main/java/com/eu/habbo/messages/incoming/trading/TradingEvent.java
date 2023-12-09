package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.trades.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public abstract class TradingEvent extends MessageHandler {
    protected RoomTrade getActiveRoomTrade(Habbo habbo){
        return habbo.getRoomUnit().getRoom().getRoomTradeManager().getActiveTradeForHabbo(habbo);
    }

    protected void stopTrade(Habbo habbo) {
        Room room = habbo.getRoomUnit().getRoom();

        if (room == null)
            return;

        RoomTrade trade = room.getRoomTradeManager().getActiveTradeForHabbo(habbo);

        if (trade == null)
            return;

        trade.stopTrade(habbo);
        room.getRoomTradeManager().stopTrade(trade);
    }
}
