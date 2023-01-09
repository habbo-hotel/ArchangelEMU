package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomVisualizationSettingsComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomVisualizationSettingsComposer);
        this.response.appendBoolean(this.room.isHideWall());
        this.response.appendInt(this.room.getWallSize());
        this.response.appendInt(this.room.getFloorSize());
        return this.response;
    }
}
