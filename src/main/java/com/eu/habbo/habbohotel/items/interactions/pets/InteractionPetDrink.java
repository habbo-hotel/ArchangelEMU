package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.threading.runnables.PetClearPosture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetDrink extends InteractionDefault {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionPetDrink.class);


    public InteractionPetDrink(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPetDrink(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        this.change(room, 1);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if (this.getExtradata() == null || this.getExtradata().isEmpty())
            this.setExtradata("0");

        Pet pet = room.getPet(roomUnit);

        if (pet != null && pet.getPetData().haveDrinkItem(this) && pet.levelThirst >= 35) {
            pet.clearPosture();
            pet.getRoomUnit().setGoalLocation(room.getLayout().getTile(this.getX(), this.getY()));
            pet.getRoomUnit().setRotation(RoomUserRotation.values()[this.getRotation()]);
            pet.getRoomUnit().clearStatus();
            pet.getRoomUnit().setStatus(RoomUnitStatus.EAT, pet.getRoomUnit().getCurrentLocation().getStackHeight() + "");
            pet.packetUpdate = true;

            Emulator.getThreading().run(() -> {
                pet.addThirst(-75);
                this.change(room, -1);
                pet.getRoomUnit().clearStatus();
                new PetClearPosture(pet, RoomUnitStatus.EAT, null, true);
                pet.packetUpdate = true;
            }, 1000);

            AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId()), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetFeeding"), 75);

        }
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    private void change(Room room, int amount) {
        int state = 0;

        if (this.getExtradata() == null || this.getExtradata().isEmpty()) {
            this.setExtradata("0");
        }

        try {
            state = Integer.parseInt(this.getExtradata());
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }

        state += amount;
        if (state > this.getBaseItem().getStateCount() - 1) {
            state = this.getBaseItem().getStateCount() - 1;
        }

        if (state < 0) {
            state = 0;
        }

        this.setExtradata(state + "");
        this.needsUpdate(true);
        room.updateItemState(this);
    }

}
