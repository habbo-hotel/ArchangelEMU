package com.eu.habbo.messages.outgoing.rooms.pets.breeding;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfirmBreedingResultComposer extends MessageComposer {
    private final int anInt1;
    private final int anInt2;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.confirmBreedingResultComposer);
        this.response.appendInt(this.anInt1);
        this.response.appendInt(this.anInt2);
        return this.response;
    }
}