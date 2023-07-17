package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.PetFigureUpdateComposer;

public class TogglePetRidingPermissionEvent extends MessageHandler {
    @Override
    public void handle() {
        int petId = this.packet.readInt();

        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        Pet pet = this.client.getHabbo().getRoomUnit().getRoom().getRoomUnitManager().getRoomPetById(petId);

        if (pet == null || pet.getUserId() != this.client.getHabbo().getHabboInfo().getId() || !(pet instanceof RideablePet rideablePet))
            return;

        rideablePet.setAnyoneCanRide(!rideablePet.anyoneCanRide());
        rideablePet.setNeedsUpdate(true);

        if (!rideablePet.anyoneCanRide() && rideablePet.getRider() != null && rideablePet.getRider().getHabboInfo().getId() != this.client.getHabbo().getHabboInfo().getId()) {
            rideablePet.getRider().getHabboInfo().dismountPet(this.client.getHabbo().getRoomUnit().getRoom());
        }

        if (pet instanceof HorsePet) {
            this.client.sendResponse(new PetFigureUpdateComposer((HorsePet) pet));
        }
    }
}
