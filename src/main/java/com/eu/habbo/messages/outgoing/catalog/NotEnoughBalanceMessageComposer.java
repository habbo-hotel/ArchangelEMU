package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NotEnoughBalanceMessageComposer extends MessageComposer {
    private final boolean isCredits;
    private final boolean isPixels;
    private final int pointsType;

    public NotEnoughBalanceMessageComposer(boolean isCredits, boolean isPixels, int pointsType) {
        this.isCredits = isCredits;
        this.isPixels = isPixels;
        this.pointsType = pointsType;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.notEnoughBalanceMessageComposer);
        this.response.appendBoolean(this.isCredits);
        this.response.appendBoolean(this.isPixels);
        this.response.appendInt(this.pointsType);
        return this.response;
    }
}