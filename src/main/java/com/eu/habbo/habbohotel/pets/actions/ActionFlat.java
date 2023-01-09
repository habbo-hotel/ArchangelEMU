package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionFlat extends PetAction {
    public ActionFlat() {
        super(null, true);

        this.minimumActionDuration = 4000;
        this.statusToSet.add(RoomUnitStatus.FLAT);
    }

    // flat attack
    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.FLAT, null, false), this.minimumActionDuration);
        pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));

        if (pet.getHappiness() > 80) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
        }

        return true;
    }
}
