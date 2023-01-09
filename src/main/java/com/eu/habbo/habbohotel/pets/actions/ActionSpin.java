package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionSpin extends PetAction {
    public ActionSpin() {
        super(null, true);

        this.minimumActionDuration = 1500;
        this.statusToSet.add(RoomUnitStatus.SPIN);
    }

    // spinny spin
    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {

        if (pet.getHappiness() > 50) {
            Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.SPIN, null, false), this.minimumActionDuration);
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            return true;
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
            return false;
        }
    }
}