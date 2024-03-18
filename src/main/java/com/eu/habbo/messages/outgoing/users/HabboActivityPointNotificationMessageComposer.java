package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HabboActivityPointNotificationMessageComposer extends MessageComposer {
    private final int currentAmount;
    private final int amountAdded;
    private final int type;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboActivityPointNotificationMessageComposer);
        this.response.appendInt(this.currentAmount);
        this.response.appendInt(this.amountAdded);
        this.response.appendInt(this.type);
        return this.response;
    }
}
