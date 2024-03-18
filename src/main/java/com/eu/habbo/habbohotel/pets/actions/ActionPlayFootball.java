package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;

public class ActionPlayFootball extends PetAction {
    public ActionPlayFootball() {
        super(null, false);
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {

        Room room = pet.getRoom();

        if(room == null || room.getLayout() == null)
            return false;

        RoomItem foundBall = null;

        for(RoomItem item : room.getRoomItemManager().getFloorItems().values()) {
            if(item instanceof InteractionPushable) {
                foundBall = item;
            }
        }

        if(foundBall == null)
            return false;

        pet.getRoomUnit().walkTo(room.getLayout().getTile(foundBall.getCurrentPosition().getX(), foundBall.getCurrentPosition().getY()));

        if (pet.getHappiness() > 75)
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        else
            pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));

        return true;
    }
}
