package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PurchaseNotAllowedMessageComposer extends MessageComposer {
    public final static int ILLEGAL = 0;
    public final static int REQUIRES_CLUB = 1;

    private final int code;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.purchaseNotAllowedMessageComposer);
        this.response.appendInt(this.code);
        return this.response;
    }
}
