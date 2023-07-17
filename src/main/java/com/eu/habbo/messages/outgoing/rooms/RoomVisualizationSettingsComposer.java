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
        this.response.appendBoolean(this.room.getRoomInfo().isHideWalls());
        this.response.appendInt(this.room.getRoomInfo().getWallThickness());
        this.response.appendInt(this.room.getRoomInfo().getFloorThickness());
        return this.response;
    }
}
