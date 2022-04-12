package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionCroak extends PetAction {
    public ActionCroak() {
        super(PetTasks.SPEAK, false);
        this.minimumActionDuration = 2000;
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        pet.getRoomUnit().setStatus(RoomUnitStatus.CROAK, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");

        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.CROAK, null, false), this.minimumActionDuration);

        if (pet.getHappyness() > 70)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_HAPPY));
        else if (pet.getHappyness() < 30)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_SAD));
        else if (pet.getLevelHunger() > 65)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.HUNGRY));
        else if (pet.getLevelThirst() > 65)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.THIRSTY));
        else if (pet.getEnergy() < 25)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.TIRED));
        else if (pet.getTask() == PetTasks.NEST || pet.getTask() == PetTasks.DOWN)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.SLEEPING));

        return true;
    }
}
