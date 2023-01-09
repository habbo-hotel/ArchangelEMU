package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionDance extends PetAction {
    public ActionDance() {
        super(null, true);

        this.minimumActionDuration = 3000;
        this.statusToSet.add(RoomUnitStatus.DANCE);
}

    // mambojambo works better than ur dancing skills
    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getHappiness() > 55) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
        }

        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.DANCE, null, false), this.minimumActionDuration);
        return true;
    }
}
