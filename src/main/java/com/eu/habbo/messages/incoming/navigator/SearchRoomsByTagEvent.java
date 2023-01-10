package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.GuestRoomSearchResultComposer;

public class SearchRoomsByTagEvent extends MessageHandler {
    @Override
    public void handle() {
        String tag = this.packet.readString();

        this.client.sendResponse(new GuestRoomSearchResultComposer(Emulator.getGameEnvironment().getRoomManager().getRoomsWithTag(tag)));
    }
}
