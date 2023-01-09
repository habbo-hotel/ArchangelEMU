package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AvatarEffectSelectedMessageComposer extends MessageComposer {
    private final int type;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.avatarEffectSelectedMessageComposer);
        this.response.appendInt(this.type);
        return this.response;
    }
}