package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PetExperienceComposer extends MessageComposer {
    private final Pet pet;
    private final int amount;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petExperienceComposer);
        this.response.appendInt(this.pet.getId());
        this.response.appendInt(this.pet.getRoomUnit().getId());
        this.response.appendInt(this.amount);
        return this.response;
    }
}
