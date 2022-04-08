package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionBounce extends PetAction {
    public ActionBounce() {
        super(null, true);

        this.minimumActionDuration = 1000;
        this.statusToSet.add(RoomUnitStatus.BOUNCE);
    }

    // bouncy bounce
    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {

        if (pet.getHappyness() > 50) {
            Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.BOUNCE, null, false), this.minimumActionDuration);
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            return true;
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
            return false;
        }
    }
}
