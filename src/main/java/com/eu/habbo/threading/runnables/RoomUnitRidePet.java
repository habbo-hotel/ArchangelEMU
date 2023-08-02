package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomUnitRidePet implements Runnable {
    private final RideablePet pet;
    private final Habbo habbo;
    private final RoomTile goalTile;


    @Override
    public void run() {
        if (this.habbo.getRoomUnit() == null || this.pet.getRoomUnit() == null || this.pet.getRoom() != this.habbo.getRoomUnit().getRoom() || this.goalTile == null || this.habbo.getRoomUnit().getTargetPosition() != this.goalTile)
            return;

        if (habbo.getRoomUnit().getCurrentPosition().distance(pet.getRoomUnit().getCurrentPosition()) <= 1) {
            habbo.getRoomUnit().stopWalking();
            habbo.getRoomUnit().giveEffect(77, -1);
            habbo.getRoomUnit().setRidingPet(pet);
            habbo.getRoomUnit().setCurrentPosition(this.pet.getRoomUnit().getCurrentPosition());
            habbo.getRoomUnit().setCurrentZ(this.pet.getRoomUnit().getCurrentZ() + 1);
            habbo.getRoomUnit().setRotation(this.pet.getRoomUnit().getBodyRotation());
            habbo.getRoomUnit().setRideLocked(false);
            pet.setRider(habbo);
            habbo.getRoomUnit().getRoom().sendComposer(new UserUpdateComposer(habbo.getRoomUnit()).compose());
            habbo.getRoomUnit().getRoom().sendComposer(new AvatarEffectMessageComposer(habbo.getRoomUnit()).compose());
            pet.setTask(PetTasks.RIDE);
        } else {
            pet.getRoomUnit().setWalkTimeOut(3 + Emulator.getIntUnixTimestamp());
            pet.getRoomUnit().stopWalking();
            habbo.getRoomUnit().walkTo(goalTile);
            Emulator.getThreading().run(this, 500);
        }
    }
}
