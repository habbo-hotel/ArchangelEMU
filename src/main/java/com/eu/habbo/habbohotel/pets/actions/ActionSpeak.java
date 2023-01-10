package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionSpeak extends ActionVocals {
    public ActionSpeak() {
        super(PetTasks.SPEAK, false);

        this.statusToSet.add(RoomUnitStatus.SPEAK);
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        pet.setMuted(false);
        Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.SPEAK, null, false), 2000);

        petSay(pet);

        return true;
    }
}
