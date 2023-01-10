package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.messages.incoming.MessageHandler;

public class DeclineFriendEvent extends MessageHandler {
    @Override
    public void handle() {
        boolean all = this.packet.readBoolean();

        if (all) {
            this.client.getHabbo().getMessenger().deleteAllFriendRequests(this.client.getHabbo().getHabboInfo().getId());
        } else {
            int count = this.packet.readInt();

            for (int i = 0; i < count; i++) {
                this.client.getHabbo().getMessenger().deleteFriendRequests(this.packet.readInt(), this.client.getHabbo().getHabboInfo().getId());
            }
        }
    }
}