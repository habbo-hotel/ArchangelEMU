package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.pets.PerkAllowancesComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.breeding.ConfirmBreedingRequestComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.breeding.NestBreedingSuccessComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetBreedingNest extends HabboItem {
    private Pet petOne = null;
    private Pet petTwo = null;

    public InteractionPetBreedingNest(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPetBreedingNest(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return room.getPet(roomUnit) != null && !this.boxFull();
    }

    @Override
    public boolean isWalkable() {
        return true;
    }


    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        Pet pet = room.getPet(roomUnit);

        if (pet != null && !this.boxFull()) {
            this.addPet(pet);

            if (this.boxFull()) {
                Habbo ownerPetOne = room.getHabbo(this.petOne.getUserId());
                Habbo ownerPetTwo = room.getHabbo(this.petTwo.getUserId());

                if (ownerPetOne != null && ownerPetTwo != null && this.petOne.getPetData().getType() == this.petTwo.getPetData().getType() && this.petOne.getPetData().getOffspringType() != -1) {
                    ownerPetTwo.getClient().sendResponse(new ConfirmBreedingRequestComposer(this.getId(), this.petOne.getPetData().getOffspringType(), this.petOne, ownerPetOne.getHabboInfo().getUsername(), this.petTwo, ownerPetTwo.getHabboInfo().getUsername()));
                    this.setExtradata("1");
                    room.updateItem(this);
                }
            }
        }
    }

    public boolean addPet(Pet pet) {
        if (this.petOne == null) {
            this.petOne = pet;
            this.petOne.getRoomUnit().setCanWalk(false);
            return true;
        } else if (this.petTwo == null && this.petOne != pet) {
            this.petTwo = pet;
            this.petTwo.getRoomUnit().setCanWalk(false);
            return true;
        }

        return false;
    }

    public boolean boxFull() {
        return this.petOne != null && this.petTwo != null;
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) {
        if (this.petOne != null && this.petOne.getRoomUnit() == roomUnit) this.petOne = null;
        if (this.petTwo != null && this.petTwo.getRoomUnit() == roomUnit) this.petTwo = null;

        this.setExtradata("0");
        room.updateItem(this);
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    public void stopBreeding(Habbo habbo) {
        this.setExtradata("0");
        habbo.getHabboInfo().getCurrentRoom().updateItem(this);

        if (this.petOne != null) {
            habbo.getClient().sendResponse(new PerkAllowancesComposer(this.getId(), PerkAllowancesComposer.CLOSE_WIDGET, ""));
        }
        if (this.petTwo.getUserId() != habbo.getHabboInfo().getId()) {
            Habbo owner = this.petTwo.getRoom().getHabbo(this.petTwo.getUserId());
            if (owner != null) {
                owner.getClient().sendResponse(new PerkAllowancesComposer(this.getId(), PerkAllowancesComposer.CLOSE_WIDGET, ""));
            }
        }

        this.freePets();

    }

    private void freePets() {
        if (this.petOne != null) {
            this.petOne.getRoomUnit().setCanWalk(true);
            this.petOne.setTask(PetTasks.FREE);
            this.petOne = null;
        }

        if (this.petTwo != null) {
            this.petTwo.getRoomUnit().setCanWalk(true);
            this.petTwo.setTask(PetTasks.FREE);
            this.petTwo = null;
        }
    }

    public void breed(Habbo habbo, String name) {
        Emulator.getThreading().run(new QueryDeleteHabboItem(this.getId()));

        this.setExtradata("2");
        habbo.getHabboInfo().getCurrentRoom().updateItem(this);

        HabboItem box = this;

        Emulator.getThreading().run(() -> {
            Pet offspring = Emulator.getGameEnvironment().getPetManager().createPet(petOne.getPetData().getOffspringType(), (int) Math.min(Math.round(Math.max(1d, PetManager.getNormalDistributionForBreeding(petOne.getLevel(), petTwo.getLevel()).sample())), 20), name, habbo.getClient());

            habbo.getHabboInfo().getCurrentRoom().placePet(offspring, box.getX(), box.getY(), box.getZ());
            offspring.setNeedsUpdate(true);
            offspring.run();
            InteractionPetBreedingNest.this.freePets();
            habbo.getHabboInfo().getCurrentRoom().removeHabboItem(box);
            habbo.getClient().sendResponse(new NestBreedingSuccessComposer(offspring.getId(), Emulator.getGameEnvironment().getPetManager().getRarityForOffspring(offspring)));

            if (box.getBaseItem().getName().startsWith("pet_breeding_")) {
                String boxType = box.getBaseItem().getName().replace("pet_breeding_", "");
                String achievement = boxType.substring(0, 1).toUpperCase() + boxType.substring(1) + "Breeder";
                AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement(achievement));
            }
        }, 2000);

    }
}