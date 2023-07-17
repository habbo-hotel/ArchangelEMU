package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModeratorRoomInfoComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.moderatorRoomInfoComposer);
        this.response.appendInt(this.room.getRoomInfo().getId());
        this.response.appendInt(this.room.getRoomUnitManager().getCurrentRoomHabbos().size());
        this.response.appendBoolean(this.room.getRoomUnitManager().getRoomHabboById(this.room.getRoomInfo().getOwnerInfo().getId()) != null);
        this.response.appendInt(this.room.getRoomInfo().getOwnerInfo().getId());
        this.response.appendString(this.room.getRoomInfo().getOwnerInfo().getUsername());
        this.response.appendBoolean(true);
        this.response.appendString(this.room.getRoomInfo().getName());
        this.response.appendString(this.room.getRoomInfo().getDescription());
        this.response.appendInt(this.room.getRoomInfo().getTags().split(";").length);
        for (int i = 0; i < this.room.getRoomInfo().getTags().split(";").length; i++) {
            this.response.appendString(this.room.getRoomInfo().getTags().split(";")[i]);
        }
        return this.response;
    }
}
