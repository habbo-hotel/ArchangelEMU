package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PetReceivedMessageComposer extends MessageComposer {
    private final Pet pet;
    private final boolean gift;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petReceivedMessageComposer);
        this.response.appendBoolean(this.gift);
        this.pet.serialize(this.response);
        return this.response;
    }
}
