package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.GuestRoomSearchResultComposer;

public class MyRoomHistorySearchEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new GuestRoomSearchResultComposer(Emulator.getGameEnvironment().getRoomManager().getRoomsVisited(this.client.getHabbo(), false, 25)));
    }
}
