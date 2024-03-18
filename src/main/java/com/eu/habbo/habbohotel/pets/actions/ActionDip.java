package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;

public class ActionDip extends PetAction {
    public ActionDip() {
        super(null, true);
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        THashSet<RoomItem> waterItems = pet.getRoom().getRoomSpecialTypes().getItemsOfType(InteractionWater.class);

        if (waterItems.isEmpty()) {
            return false;
        }

        RoomItem waterPatch = (RoomItem) waterItems.toArray()[Emulator.getRandom().nextInt(waterItems.size())];

        pet.getRoomUnit().walkTo(pet.getRoom().getLayout().getTile(waterPatch.getCurrentPosition().getX(), waterPatch.getCurrentPosition().getY()));

        if (pet.getHappiness() > 70) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));
        }

        return true;
    }
}
