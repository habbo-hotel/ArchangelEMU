package com.eu.habbo.plugin.events.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import lombok.Getter;

@Getter
public class PetTalkEvent extends PetEvent {

    private final RoomChatMessage message;

    public PetTalkEvent(Pet pet, RoomChatMessage message) {
        super(pet);

        this.message = message;
    }
}
