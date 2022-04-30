package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetRemovedFromInventoryComposer extends MessageComposer {
    private final int petId;

    public PetRemovedFromInventoryComposer(Pet pet) {
        this.petId = pet.getId();
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petRemovedFromInventoryComposer);
        this.response.appendInt(this.petId);
        return this.response;
    }
}
