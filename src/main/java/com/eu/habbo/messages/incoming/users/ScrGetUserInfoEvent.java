package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.ScrSendUserInfoComposer;

public class ScrGetUserInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        String subscriptionType = this.packet.readString();
        this.client.sendResponse(new ScrSendUserInfoComposer(this.client.getHabbo(), subscriptionType));
    }
}
