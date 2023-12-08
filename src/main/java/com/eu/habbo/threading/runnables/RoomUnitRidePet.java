package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;

public class RoomUnitRidePet implements Runnable {
    private final int MAX_RETRIES = 3;
    private final RideablePet pet;
    private final Habbo habbo;
    private final RoomTile goalTile;
    private int retries;

    public RoomUnitRidePet(RideablePet pet, Habbo rider, RoomTile petTile) {
        this.pet = pet;
        this.habbo = rider;
        this.goalTile = petTile;
        this.retries = 0;
    }

    @Override
    public void run() {
        if (this.pet.getRoom() != this.habbo.getRoomUnit().getRoom()) {
            this.habbo.getRoomUnit().setRideLocked(false);
            return;
        }

        if(this.retries >= MAX_RETRIES) {
            this.habbo.getRoomUnit().setRideLocked(false);
            return;
        }

        this.retries++;

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
