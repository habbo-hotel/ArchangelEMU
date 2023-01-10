package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.CanCreateRoomComposer;

public class CanCreateRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        int count = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo()).size();
        int max = this.client.getHabbo().getHabboStats().hasActiveClub() ? RoomManager.MAXIMUM_ROOMS_HC : RoomManager.MAXIMUM_ROOMS_USER;
        this.client.sendResponse(new CanCreateRoomComposer(count, max));
    }
}
