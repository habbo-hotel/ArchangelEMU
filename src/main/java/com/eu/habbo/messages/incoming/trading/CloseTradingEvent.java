package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class CloseTradingEvent extends TradingEvent {
    @Override
    public void handle() {
        stopTrade(this.client.getHabbo());
    }
}
