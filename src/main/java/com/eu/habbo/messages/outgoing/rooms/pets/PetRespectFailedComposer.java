package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PetRespectFailedComposer extends MessageComposer {
    private final int currentAge;
    private final int requiredAge;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petRespectFailedComposer);
        this.response.appendInt(this.currentAge);
        this.response.appendInt(this.requiredAge);
        return this.response;
    }
}
