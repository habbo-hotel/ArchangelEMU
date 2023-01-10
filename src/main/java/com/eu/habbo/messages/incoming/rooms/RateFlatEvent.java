package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RateFlatEvent extends MessageHandler {
    @Override
    public void handle() {
        Emulator.getGameEnvironment().getRoomManager().voteForRoom(this.client.getHabbo(), this.client.getHabbo().getHabboInfo().getCurrentRoom());
    }
}
