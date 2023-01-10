package com.eu.habbo.messages.incoming.floorplaneditor;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.floorplaneditor.RoomEntryTileMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomFloorThicknessUpdatedComposer;

public class GetRoomEntryTileEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        this.client.sendResponse(new RoomEntryTileMessageComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom()));
        this.client.sendResponse(new RoomFloorThicknessUpdatedComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom()));
    }
}
