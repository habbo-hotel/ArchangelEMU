package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.rooms.RoomMoodlightData;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.RoomDimmerPresetsComposer;
import gnu.trove.map.TIntObjectMap;

public class RoomDimmerGetPresetsEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getRoomUnit().getRoom() != null)
            this.client.sendResponse(new RoomDimmerPresetsComposer((TIntObjectMap<RoomMoodlightData>) this.client.getHabbo().getRoomUnit().getRoom().getRoomInfo().getMoodLightData()));
    }
}
