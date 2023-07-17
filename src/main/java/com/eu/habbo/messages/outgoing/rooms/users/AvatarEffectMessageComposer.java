package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AvatarEffectMessageComposer extends MessageComposer {
    private final RoomAvatar roomAvatar;
    private final int effectId;

    public AvatarEffectMessageComposer(RoomAvatar roomAvatar) {
        this.roomAvatar = roomAvatar;
        this.effectId = -1;
    }


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectMessageComposer);
        this.response.appendInt(this.roomAvatar.getVirtualId());
        this.response.appendInt(this.effectId == -1 ? this.roomAvatar.getEffectId() : this.effectId);
        this.response.appendInt(0);
        return this.response;
    }
}
