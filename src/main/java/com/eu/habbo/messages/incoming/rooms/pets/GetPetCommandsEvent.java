package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.PetTrainingPanelComposer;

public class GetPetCommandsEvent extends MessageHandler {
    @Override
    public void handle() {
        int petId = this.packet.readInt();

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        Pet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);

        if (pet != null)
            this.client.sendResponse(new PetTrainingPanelComposer(pet));
    }
}
