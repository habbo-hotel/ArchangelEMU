package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MarketplaceMakeOfferResult extends MessageComposer {
    public static final int POST_SUCCESS = 1;
    public static final int FAILED_TECHNICAL_ERROR = 2;
    public static final int MARKETPLACE_DISABLED = 3;
    public static final int ITEM_JUST_ADDED_TO_SHOP = 4;

    private final int code;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.marketplaceMakeOfferResult);
        this.response.appendInt(this.code);
        return this.response;
    }
}
