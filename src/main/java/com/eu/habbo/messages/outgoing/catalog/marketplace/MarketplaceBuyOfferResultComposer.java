package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MarketplaceBuyOfferResultComposer extends MessageComposer {
    public static final int REFRESH = 1;
    public static final int SOLD_OUT = 2;
    public static final int UPDATES = 3;
    public static final int NOT_ENOUGH_CREDITS = 4;

    private final int errorCode;
    private final int unknown;
    private final int offerId;
    private final int price;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.marketplaceBuyOfferResultComposer);
        this.response.appendInt(this.errorCode); //result
        this.response.appendInt(this.unknown); //newOfferId
        this.response.appendInt(this.offerId); //newPrice
        this.response.appendInt(this.price); //requestedOfferId
        return this.response;
    }
}
