package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PetStatusUpdateComposer extends MessageComposer {
    private final Pet pet;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.petStatusUpdateComposer);
        this.response.appendInt(this.pet.getRoomUnit().getId());
        this.response.appendInt(this.pet instanceof RideablePet && ((RideablePet) this.pet).anyoneCanRide() ? 1 : 0);
        this.response.appendBoolean((this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).canBreed())); //unknown 1
        this.response.appendBoolean((this.pet instanceof MonsterplantPet && !((MonsterplantPet) this.pet).isFullyGrown()));
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isDead()); //State Grown
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isPubliclyBreedable());
        return this.response;
    }
}
