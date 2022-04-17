package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.ScrSendUserInfoComposer;

public class RequestUserClubEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        String subscriptionType = this.packet.readString();
        this.client.sendResponse(new ScrSendUserInfoComposer(this.client.getHabbo(), subscriptionType));
    }
}
