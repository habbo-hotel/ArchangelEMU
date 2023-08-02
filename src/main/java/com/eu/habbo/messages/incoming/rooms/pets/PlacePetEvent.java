package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomPet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.PetPlacingErrorComposer;
import com.eu.habbo.messages.outgoing.inventory.PetRemovedFromInventoryComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;

public class PlacePetEvent extends MessageHandler {
    @Override
    public void handle() {
        //TODO Improve This
        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null)
            return;

        if (this.client.getHabbo().getHabboInfo().getId() != room.getRoomInfo().getOwnerInfo().getId()) {
            if (!room.getRoomInfo().isAllowPets() && !(this.client.getHabbo().hasPermissionRight(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermissionRight(Permission.ACC_PLACEFURNI))) {
                this.client.sendResponse(new PetPlacingErrorComposer(PetPlacingErrorComposer.ROOM_ERROR_PETS_FORBIDDEN_IN_FLAT));
                return;
            }
        }

        int petId = this.packet.readInt();

        Pet pet = this.client.getHabbo().getInventory().getPetsComponent().getPet(petId);

        if (pet == null) {
            return;
        }
        if (room.getRoomUnitManager().getCurrentPets().size() >= Room.MAXIMUM_PETS && !this.client.getHabbo().hasPermissionRight(Permission.ACC_UNLIMITED_PETS)) {
            this.client.sendResponse(new PetPlacingErrorComposer(PetPlacingErrorComposer.ROOM_ERROR_MAX_PETS));
            return;
        }

        int x = this.packet.readInt();
        int y = this.packet.readInt();

        RoomTile tile;
        RoomTile playerTile = this.client.getHabbo().getRoomUnit().getCurrentPosition();

        if ((x == 0 && y == 0) || !room.getRoomInfo().isRoomOwner(this.client.getHabbo())) {
            //Place the pet in front of the player.
            tile = room.getLayout().getTileInFront(this.client.getHabbo().getRoomUnit().getCurrentPosition(), this.client.getHabbo().getRoomUnit().getBodyRotation().getValue());

            if (tile == null || !tile.isWalkable()) {
                this.client.sendResponse(new PetPlacingErrorComposer(PetPlacingErrorComposer.ROOM_ERROR_PETS_NO_FREE_TILES));
            }

            //Check if tile exists and is walkable. Else place it in the current location the Habbo is standing.
            if (tile == null || !tile.isWalkable()) {
                tile = playerTile;

                //If the current tile is not walkable, place it at the door.
                if (tile == null || !tile.isWalkable()) {
                    tile = room.getLayout().getDoorTile();
                }
            }
        } else {
            tile = room.getLayout().getTile((short) x, (short) y);
        }

        if (tile == null || !tile.isWalkable() || !tile.getAllowStack()) {
            this.client.sendResponse(new PetPlacingErrorComposer(PetPlacingErrorComposer.ROOM_ERROR_PETS_SELECTED_TILE_NOT_FREE));
            return;
        }

        pet.setRoom(room);
        RoomPet roomPet = pet.getRoomUnit();

        if (roomPet == null) {
            roomPet = new RoomPet();
            roomPet.setUnit(pet);
        }

        roomPet.setRoom(room);

        roomPet.setLocation(tile);
        roomPet.setCurrentZ(tile.getStackHeight());
        roomPet.addStatus(RoomUnitStatus.SIT, "0");
        roomPet.setRoomUnitType(RoomUnitType.PET);
        if (playerTile != null) {
            roomPet.lookAtPoint(playerTile);
        }
        pet.setRoomUnit(roomPet);
        room.getRoomUnitManager().addRoomUnit(pet);
        pet.setNeedsUpdate(true);
        Emulator.getThreading().run(pet);
        room.sendComposer(new RoomPetComposer(pet).compose());
        this.client.getHabbo().getInventory().getPetsComponent().removePet(pet);
        this.client.sendResponse(new PetRemovedFromInventoryComposer(pet));
    }
}
