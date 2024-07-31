package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TurfCaptureTimeLeftComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.turfCaptureTimeLeftComposer);
        Habbo capturingHabbo = this.room != null ? this.room.getRoomTurfManager().getCapturingHabbo() : null;
        this.response.appendInt(capturingHabbo != null ? capturingHabbo.getHabboInfo().getId() : -1);
        this.response.appendString(capturingHabbo != null ? capturingHabbo.getHabboInfo().getUsername() : "");
        this.response.appendString(capturingHabbo != null ? capturingHabbo.getHabboInfo().getLook() : "");
        this.response.appendInt(this.room != null ? this.room.getRoomTurfManager().getSecondsLeft() : 0);
        this.response.appendBoolean(this.room == null || !this.room.getRoomTurfManager().isBlocked());
        return this.response;
    }
}
