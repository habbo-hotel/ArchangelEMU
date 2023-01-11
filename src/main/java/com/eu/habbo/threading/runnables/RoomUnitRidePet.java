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
        if (this.habbo.getRoomUnit() == null || this.pet.getRoomUnit() == null || this.pet.getRoom() != this.habbo.getHabboInfo().getCurrentRoom() || this.goalTile == null || this.habbo.getRoomUnit().getGoalLocation() != this.goalTile)
            return;

        if (habbo.getRoomUnit().getCurrentLocation().distance(pet.getRoomUnit().getCurrentLocation()) <= 1) {
            habbo.getRoomUnit().stopWalking();
            habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, 77, -1);
            habbo.getHabboInfo().setRiding(pet);
            habbo.getRoomUnit().setCurrentLocation(this.pet.getRoomUnit().getCurrentLocation());
            habbo.getRoomUnit().setPreviousLocation(this.pet.getRoomUnit().getCurrentLocation());
            habbo.getRoomUnit().setZ(this.pet.getRoomUnit().getZ() + 1);
            habbo.getRoomUnit().setPreviousLocationZ(this.pet.getRoomUnit().getZ() + 1);
            habbo.getRoomUnit().setRotation(this.pet.getRoomUnit().getBodyRotation());
            habbo.getRoomUnit().statusUpdate(true);
            pet.setRider(habbo);
            habbo.getHabboInfo().getCurrentRoom().sendComposer(new UserUpdateComposer(habbo.getRoomUnit()).compose());
            habbo.getHabboInfo().getCurrentRoom().sendComposer(new AvatarEffectMessageComposer(habbo.getRoomUnit()).compose());
            pet.setTask(PetTasks.RIDE);
        } else {
            pet.getRoomUnit().setWalkTimeOut(3 + Emulator.getIntUnixTimestamp());
            pet.getRoomUnit().stopWalking();
            habbo.getRoomUnit().setGoalLocation(goalTile);
            Emulator.getThreading().run(this, 500);
        }
    }
}
