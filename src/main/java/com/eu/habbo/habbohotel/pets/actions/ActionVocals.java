package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;

public abstract class ActionVocals extends PetAction {

    protected ActionVocals(PetTasks petTask, boolean stopsPetWalking) {
        super(petTask, stopsPetWalking);
    }

    protected void petSay(Pet pet) {
        if (pet.getHappiness() > 70)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_HAPPY));
        else if (pet.getHappiness() < 30)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_SAD));
        else if (pet.getLevelHunger() > 65)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.HUNGRY));
        else if (pet.getLevelThirst() > 65)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.THIRSTY));
        else if (pet.getEnergy() < 25)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.TIRED));
        else if (pet.getTask() == PetTasks.NEST || pet.getTask() == PetTasks.DOWN)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.SLEEPING));
    }
}
