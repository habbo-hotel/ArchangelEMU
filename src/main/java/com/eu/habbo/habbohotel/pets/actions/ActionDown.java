package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;

public class ActionDown extends PetAction {
    public ActionDown() {
        super(PetTasks.DOWN, true);
        this.statusToRemove.add(RoomUnitStatus.BEG);
        this.statusToRemove.add(RoomUnitStatus.MOVE);
        this.statusToRemove.add(RoomUnitStatus.DEAD);
        this.minimumActionDuration = 4000;
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getTask() != PetTasks.DOWN && !pet.getRoomUnit().hasStatus(RoomUnitStatus.LAY)) {
            pet.getRoomUnit().setCmdLay(true);
            pet.getRoomUnit().setStatus(RoomUnitStatus.LAY, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");

            Emulator.getThreading().run(() -> {
                pet.getRoomUnit().setCmdLay(false);
                pet.clearPosture();
            }, this.minimumActionDuration);

            if (pet.getHappiness() > 40) {
                pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
            } else {
                pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }

            return true;
        }

        return false;
    }
}
