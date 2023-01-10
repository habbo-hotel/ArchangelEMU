package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.RoomDimmerPresetsComposer;

public class RoomDimmerGetPresetsEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
            this.client.sendResponse(new RoomDimmerPresetsComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom().getMoodlightData()));
    }
}
