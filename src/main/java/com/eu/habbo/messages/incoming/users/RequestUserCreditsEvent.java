package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.messages.outgoing.users.ActivityPointsMessageComposer;

public class RequestUserCreditsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new CreditBalanceComposer(this.client.getHabbo()));
        this.client.sendResponse(new ActivityPointsMessageComposer(this.client.getHabbo()));
    }
}
