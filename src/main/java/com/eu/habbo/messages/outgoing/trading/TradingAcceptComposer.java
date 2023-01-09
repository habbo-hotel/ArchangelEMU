package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TradingAcceptComposer extends MessageComposer {
    private final RoomTradeUser tradeUser;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.tradingAcceptComposer);
        this.response.appendInt(this.tradeUser.getUserId());
        this.response.appendInt(this.tradeUser.isAccepted());
        return this.response;
    }
}
