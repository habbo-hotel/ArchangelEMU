package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;

public class MovePetEvent extends MessageHandler {
    @Override
    public void handle() {
        int petId = this.packet.readInt();
        Pet pet = this.client.getHabbo().getRoomUnit().getRoom().getRoomUnitManager().getRoomPetById(petId);

        if (pet != null) {
            Room room = this.client.getHabbo().getRoomUnit().getRoom();
            if (room != null && room.getRoomRightsManager().hasRights(this.client.getHabbo())) {
                if (pet.getRoomUnit() != null) {
                    int x = this.packet.readInt();
                    int y = this.packet.readInt();

                    RoomTile tile = room.getLayout().getTile((short) x, (short) y);

                    if (tile != null) {
                        pet.getRoomUnit().setLocation(tile);
                        pet.getRoomUnit().setCurrentZ(tile.getZ());
                        pet.getRoomUnit().setRotation(RoomRotation.fromValue(this.packet.readInt()));
                        room.sendComposer(new UserUpdateComposer(pet.getRoomUnit()).compose());
                        pet.setSqlUpdateNeeded(true);
                    }
                }
            }
        }
    }
}