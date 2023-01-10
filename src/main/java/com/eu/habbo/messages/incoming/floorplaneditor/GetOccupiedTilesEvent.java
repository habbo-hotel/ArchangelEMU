package com.eu.habbo.messages.incoming.floorplaneditor;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.floorplaneditor.RoomOccupiedTilesMessageComposer;

public class GetOccupiedTilesEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        this.client.sendResponse(new RoomOccupiedTilesMessageComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom()));
    }
}
