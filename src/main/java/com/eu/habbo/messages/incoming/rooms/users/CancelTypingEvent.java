package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.UserTypingMessageComposer;

public class CancelTypingEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return;
        }

        if (this.client.getHabbo().getRoomUnit() == null) {
            return;
        }

        this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserTypingMessageComposer(this.client.getHabbo().getRoomUnit(), false).compose());
    }
}
