package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CameraPublishStatusMessageComposer extends MessageComposer {
    private final boolean isOk;
    private final int cooldownSeconds;
    private final String extraDataId;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.cameraPublishStatusMessageComposer);

        this.response.appendBoolean(this.isOk);
        this.response.appendInt(this.cooldownSeconds);

        if (!this.extraDataId.isEmpty()) {
            this.response.appendString(this.extraDataId);
        }

        return this.response;
    }
}