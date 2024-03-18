package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TradingCloseComposer extends MessageComposer {
    public static final int USER_CANCEL_TRADE = 0;
    public static final int ITEMS_NOT_FOUND = 1;

    private final int userId;
    private final int errorCode;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.tradingCloseComposer);
        this.response.appendInt(this.userId);
        this.response.appendInt(this.errorCode);
        return this.response;
    }
}
