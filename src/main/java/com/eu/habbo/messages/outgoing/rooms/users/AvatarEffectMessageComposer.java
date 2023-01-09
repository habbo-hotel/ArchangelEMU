package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AvatarEffectMessageComposer extends MessageComposer {
    private final RoomUnit roomUnit;
    private final int effectId;

    public AvatarEffectMessageComposer(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
        this.effectId = -1;
    }


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectMessageComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.effectId == -1 ? this.roomUnit.getEffectId() : this.effectId);
        this.response.appendInt(0);
        return this.response;
    }
}
