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
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.threading.runnables.PetClearPosture;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetToy extends InteractionDefault {
    public InteractionPetToy(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionPetToy(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.setExtraData("0");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {}
    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        this.setExtraData("0");
        room.updateItem(this);

        for (Pet pet : room.getRoomUnitManager().getPetsAt(oldLocation)) {
            pet.getRoomUnit().clearStatuses();
            pet.setPacketUpdate(true);
        }
    }
    @Override
    public void onPickUp(Room room) {
        this.setExtraData("0");

        for (Pet pet : room.getPetsOnItem(this)) {
            pet.getRoomUnit().clearStatuses();
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

                pet.setTask(PetTasks.PLAY);
                pet.getRoomUnit().walkTo(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
                pet.getRoomUnit().setRotation(RoomRotation.values()[this.getRotation()]);
                pet.getRoomUnit().clearStatuses();
                pet.getRoomUnit().addStatus(RoomUnitStatus.PLAY, pet.getRoomUnit().getCurrentPosition().getStackHeight() + "");
                pet.setPacketUpdate(true);
                RoomItem item = this;
                Emulator.getThreading().run(() -> {
                    pet.addHappiness(25);
                    item.setExtraData("0");
                    room.updateItem(item);
                    pet.getRoomUnit().clearStatuses();
                    new PetClearPosture(pet, RoomUnitStatus.PLAY, null, true).run();
                    pet.setPacketUpdate(true);
                }, ((long) (Emulator.getRandom().nextInt(20) * 500) + 2500));
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
            pet.getRoomUnit().clearStatuses();
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
        roomItemManager.getPetToys().remove(getId());
    }

    @Override
    public void addThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getPetToys().put(getId(), this);
    }
}