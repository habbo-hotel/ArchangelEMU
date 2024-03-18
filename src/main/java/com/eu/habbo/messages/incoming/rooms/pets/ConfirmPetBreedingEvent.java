package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetBreedingNest;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ConfirmPetBreedingEvent extends MessageHandler {

    @Override
    public void handle() {
        int itemId = this.packet.readInt();
        String name = this.packet.readString();
        int petOneId = this.packet.readInt();
        int petTwoId = this.packet.readInt();

        RoomItem item = this.client.getHabbo().getRoomUnit().getRoom().getRoomItemManager().getRoomItemById(itemId);

        if (item instanceof InteractionPetBreedingNest) {
            ((InteractionPetBreedingNest) item).breed(this.client.getHabbo(), name);
        }
    }
}