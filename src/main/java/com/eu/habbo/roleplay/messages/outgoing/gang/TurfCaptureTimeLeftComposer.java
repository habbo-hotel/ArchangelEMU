package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.habbohotel.rooms.Room;
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
        this.response.appendInt(this.room.getRoomTurfManager().getCapturingHabbo().getHabboInfo().getId());
        this.response.appendString(this.room.getRoomTurfManager().getCapturingHabbo().getHabboInfo().getUsername());
        this.response.appendString(this.room.getRoomTurfManager().getCapturingHabbo().getHabboInfo().getLook());
        this.response.appendInt(this.room.getRoomTurfManager().getSecondsLeft());
        this.response.appendBoolean(this.room.getRoomTurfManager().isBlocked());
        return this.response;
    }
}
