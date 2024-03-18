package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MarketplaceCanMakeOfferResult extends MessageComposer {
    public static final int NOT_ALLOWED = 2;
    public static final int NO_TRADE_PASS = 3;
    public static final int NO_ADS_LEFT = 4;

    private final int errorCode;
    private final int valueA;
    private final int valueB;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.marketplaceCanMakeOfferResult);
        this.response.appendInt(this.errorCode);
        this.response.appendInt(this.valueA);
        this.response.appendInt(this.valueB);
        return this.response;
    }
}
