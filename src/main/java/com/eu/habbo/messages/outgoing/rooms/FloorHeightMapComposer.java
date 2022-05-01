package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FloorHeightMapComposer extends MessageComposer {
    private final Room room;

    public FloorHeightMapComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.floorHeightMapComposer);
        this.response.appendBoolean(true);
        this.response.appendInt(this.room.getWallHeight()); //FixedWallsHeight
        this.response.appendString(this.room.getLayout().getRelativeMap());
        return this.response;
    }
}
