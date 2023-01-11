package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;

public class ActionSit extends PetAction {
    public ActionSit() {
        super(PetTasks.SIT, true);
        this.statusToRemove.add(RoomUnitStatus.BEG);
        this.statusToRemove.add(RoomUnitStatus.MOVE);
        this.statusToRemove.add(RoomUnitStatus.LAY);
        this.statusToRemove.add(RoomUnitStatus.DEAD);
        this.minimumActionDuration = 4000;
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getTask() != PetTasks.SIT && !pet.getRoomUnit().hasStatus(RoomUnitStatus.SIT)) {
            pet.getRoomUnit().setCmdSit(true);
            pet.getRoomUnit().setStatus(RoomUnitStatus.SIT, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");

            Emulator.getThreading().run(() -> {
                pet.getRoomUnit().setCmdSit(false);
                pet.clearPosture();
            }, this.minimumActionDuration);

            if (pet.getHappiness() > 75)
                pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
            else
                pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));

            return true;
        }

        return false;
    }
}
