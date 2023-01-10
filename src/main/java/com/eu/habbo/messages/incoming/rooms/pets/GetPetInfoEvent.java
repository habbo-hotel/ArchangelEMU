package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.PetInfoMessageComposer;

public class GetPetInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        int petId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        Pet pet = room.getPet(petId);

        if (pet != null) {
            this.client.sendResponse(new PetInfoMessageComposer(pet, room, this.client.getHabbo()));
        }
    }
}
