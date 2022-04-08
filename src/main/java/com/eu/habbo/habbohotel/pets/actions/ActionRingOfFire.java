package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionRingOfFire extends PetAction {
    public ActionRingOfFire() {
        super(null, true);

        this.minimumActionDuration = 2000;
        this.statusToSet.add(RoomUnitStatus.RINGOFFIRE);
    }

    // TO-DO: Make it specifically for the toy tree. I dont have the skills for that
    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getHappyness() < 50) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
            return false;
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
        }

        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.RINGOFFIRE, null, false), this.minimumActionDuration);
        return true;
    }
}