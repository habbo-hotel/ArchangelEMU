package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetTrampoline;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionJump extends PetAction {
    public ActionJump() {
        super(null, false);
        this.minimumActionDuration = 2000;
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if(pet.findPetItem(PetTasks.JUMP, InteractionPetTrampoline.class)) return false;

        pet.clearPosture();
        pet.setTask(PetTasks.JUMP);
        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.JUMP, null, false), this.minimumActionDuration);

        if (pet.getHappiness() > 60)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        else
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));

        return true;
    }
}
