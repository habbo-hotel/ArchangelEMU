package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class TradingAcceptComposer extends MessageComposer {
    private final RoomTradeUser tradeUser;

    public TradingAcceptComposer(RoomTradeUser tradeUser) {
        this.tradeUser = tradeUser;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.TradingAcceptComposer);
        this.response.appendInt(this.tradeUser.getUserId());
        this.response.appendInt(this.tradeUser.getAccepted());
        return this.response;
    }
}
