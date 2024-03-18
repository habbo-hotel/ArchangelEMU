package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CarryObjectMessageComposer extends MessageComposer {
    private final RoomAvatar roomAvatar;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.carryObjectMessageComposer);
        this.response.appendInt(this.roomAvatar.getVirtualId());
        this.response.appendInt(this.roomAvatar.getHandItem());
        return this.response;
    }
}
