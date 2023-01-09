package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionWave extends PetAction {
    public ActionWave() {
        super(PetTasks.WAVE, false);

        this.statusToSet.add(RoomUnitStatus.WAVE);
        this.minimumActionDuration = 2000;
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        pet.getRoomUnit().setStatus(RoomUnitStatus.WAVE, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");
        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.WAVE, null, false), this.minimumActionDuration);

        if (pet.getHappiness() > 40) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
        }

        return true;
    }
}
