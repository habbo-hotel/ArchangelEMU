package com.eu.habbo.messages.incoming.floorplaneditor;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.floorplaneditor.RoomOccupiedTilesMessageComposer;

public class FloorPlanEditorRequestBlockedTilesEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        this.client.sendResponse(new RoomOccupiedTilesMessageComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom()));
    }
}
