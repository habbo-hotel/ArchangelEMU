package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PetClearPosture implements Runnable {
    private final Pet pet;
    private final RoomUnitStatus key;
    private final PetTasks newTask;
    private final boolean clearTask;

    @Override
    public void run() {
        if (this.pet != null) {
            if (this.pet.getRoom() != null) {
                if (this.pet.getRoomUnit() != null) {
                    this.pet.getRoomUnit().removeStatus(this.key);

                    if (this.clearTask)
                        this.pet.setTask(PetTasks.FREE);
                    else if (this.newTask != null)
                        this.pet.setTask(this.newTask);
                }
            }
        }
    }
}
