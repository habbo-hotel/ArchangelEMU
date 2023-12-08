package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomItemManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.threading.runnables.PetClearPosture;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetTrampoline extends InteractionDefault {
    public InteractionPetTrampoline(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionPetTrampoline(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.setExtraData("0");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        this.setExtraData("0");
        room.updateItem(this);

        for (Pet pet : room.getRoomUnitManager().getPetsAt(oldLocation)) {
            pet.getRoomUnit().removeStatus(RoomUnitStatus.JUMP);
            pet.setPacketUpdate(true);
        }
    }

    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");

        for (Pet pet : room.getPetsOnItem(this)) {
            pet.getRoomUnit().removeStatus(RoomUnitStatus.JUMP);
            pet.setPacketUpdate(true);
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        Pet pet = room.getRoomUnitManager().getPetByRoomUnit(roomUnit);

        if (pet != null && pet.getPetData().haveToyItem(this.getBaseItem())) {
            if (this.getOccupyingTiles(room.getLayout()).contains(pet.getRoomUnit().getTargetPosition())) {
                if (pet.getEnergy() <= 35) {
                    return;
                }

                pet.clearPosture();
                pet.setTask(PetTasks.JUMP);
                pet.getRoomUnit().addStatus(RoomUnitStatus.JUMP, "");
                Emulator.getThreading().run(() -> {
                    new PetClearPosture(pet, RoomUnitStatus.JUMP, null, false);
                    pet.getRoomUnit().walkTo(room.getRandomWalkableTile());
                    this.setExtraData("0");
                    room.updateItemState(this);
                }, 4000);
                pet.addHappiness(25);

                this.setExtraData("1");
                room.updateItemState(this);
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);

        Pet pet = room.getRoomUnitManager().getPetByRoomUnit(roomUnit);

        if (pet != null) {
            this.setExtraData("0");
            room.updateItem(this);
            pet.getRoomUnit().removeStatus(RoomUnitStatus.JUMP);
            pet.setPacketUpdate(true);
        }
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        Pet pet = room.getRoomUnitManager().getPetByRoomUnit(roomUnit);
        return roomUnit.getRoomUnitType() == RoomUnitType.PET && pet != null && pet.getPetData().haveToyItem(this.getBaseItem());
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    @Override
    public void removeThisItem(RoomItemManager roomItemManager) {
        synchronized (roomItemManager.getUndefinedSpecials()) {
            roomItemManager.getUndefinedSpecials().remove(getId());
        }
    }

    @Override
    public void addThisItem(RoomItemManager roomItemManager) {
        synchronized (roomItemManager.getUndefinedSpecials()) {
            roomItemManager.getUndefinedSpecials().put(getId(), this);
        }
    }
}