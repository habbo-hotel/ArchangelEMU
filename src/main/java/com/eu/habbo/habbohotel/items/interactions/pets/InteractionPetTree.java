package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.threading.runnables.PetClearPosture;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetTree extends InteractionDefault {
    public InteractionPetTree(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPetTree(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        for (Pet pet : room.getPetsAt(oldLocation)) {
            pet.getRoomUnit().clearStatus();
            pet.setPacketUpdate(true);
        }
    }
    @Override
    public void onPickUp(Room room) {
        for (Pet pet : room.getPetsOnItem(this)) {
            pet.getRoomUnit().clearStatus();
            pet.setPacketUpdate(true);
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        Pet pet = room.getPet(roomUnit);
        if (pet != null && pet.getPetData().haveToyItem(this.getBaseItem()) && this.getOccupyingTiles(room.getLayout()).contains(pet.getRoomUnit().getGoalLocation())) {
            RoomUnitStatus task = switch (pet.getTask()) {
                case RING_OF_FIRE -> RoomUnitStatus.RINGOFFIRE;
                case SWING -> RoomUnitStatus.SWING;
                case ROLL -> RoomUnitStatus.ROLL;
                default -> RoomUnitStatus.HANG;
            };
            if (pet.getEnergy() >= 35 && task != RoomUnitStatus.HANG) {

                 pet.getRoomUnit().setCanWalk(false);
                 pet.getRoomUnit().setRotation(RoomUserRotation.values()[this.getRotation()]);
                 pet.getRoomUnit().clearStatus();
                 pet.getRoomUnit().setStatus(task, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");
                 pet.setPacketUpdate(true);
                RoomUnitStatus finalTask = task;
                Emulator.getThreading().run(() -> {
                     pet.addHappiness(25);
                     pet.getRoomUnit().clearStatus();
                     new PetClearPosture(pet, finalTask, null, true);
                     if (this.getOccupyingTiles(room.getLayout()).contains(pet.getRoomUnit().getCurrentLocation())) {
                         pet.getRoomUnit().setStatus(RoomUnitStatus.HANG, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");
                     } else {
                        pet.clearPosture();
                     }
                     pet.getRoomUnit().setCanWalk(true);
                     pet.setPacketUpdate(true);
                 }, (long) 2500 + (Emulator.getRandom().nextInt(20) * 500));
             } else {
                pet.getRoomUnit().setRotation(RoomUserRotation.values()[this.getRotation()]);
                pet.getRoomUnit().clearStatus();
                pet.getRoomUnit().setStatus(RoomUnitStatus.HANG, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");
                pet.setPacketUpdate(true);
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);

        Pet pet = room.getPet(roomUnit);

        if (pet != null) {
            pet.getRoomUnit().clearStatus();
            pet.setPacketUpdate(true);
        }
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        Pet pet = room.getPet(roomUnit);
        return roomUnit.getRoomUnitType() == RoomUnitType.PET && pet != null && pet.getPetData().haveToyItem(this.getBaseItem());
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }
}