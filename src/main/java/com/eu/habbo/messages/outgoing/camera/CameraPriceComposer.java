package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CameraPriceComposer extends MessageComposer {
    private final int credits;
    private final int points;
    private final int pointsType;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.initCameraMessageComposer);
        this.response.appendInt(this.credits);
        this.response.appendInt(this.points);
        this.response.appendInt(this.pointsType);
        return this.response;
    }
}