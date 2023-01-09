package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionWagTail extends PetAction {
    public ActionWagTail() {
        super(null, false);

        this.minimumActionDuration = 2000;
        this.statusToRemove.add(RoomUnitStatus.MOVE);
        this.statusToSet.add(RoomUnitStatus.WAG_TAIL);
    }

    // waggy waggy
    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        pet.clearPosture();
        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.WAG_TAIL, null, false), this.minimumActionDuration);

        if (pet.getHappiness() > 50) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
        }

        return true;
    }
}
