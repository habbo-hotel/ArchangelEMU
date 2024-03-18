package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestsComposer;

public class GetFriendRequestsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new FriendRequestsComposer(this.client.getHabbo()));
    }
}
