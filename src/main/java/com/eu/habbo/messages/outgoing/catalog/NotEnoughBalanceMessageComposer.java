package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotEnoughBalanceMessageComposer extends MessageComposer {
    private final boolean isCredits;
    private final boolean isPixels;
    private final int pointsType;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.notEnoughBalanceMessageComposer);
        this.response.appendBoolean(this.isCredits);
        this.response.appendBoolean(this.isPixels);
        this.response.appendInt(this.pointsType);
        return this.response;
    }
}