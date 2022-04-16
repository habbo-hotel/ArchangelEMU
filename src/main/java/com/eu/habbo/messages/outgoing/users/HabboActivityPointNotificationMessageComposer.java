package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HabboActivityPointNotificationMessageComposer extends MessageComposer {
    private final int currentAmount;
    private final int amountAdded;
    private final int type;

    public HabboActivityPointNotificationMessageComposer(int currentAmount, int amountAdded, int type) {
        this.currentAmount = currentAmount;
        this.amountAdded = amountAdded;
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.HabboActivityPointNotificationMessageComposer);
        this.response.appendInt(this.currentAmount);
        this.response.appendInt(this.amountAdded);
        this.response.appendInt(this.type);
        return this.response;
    }
}
