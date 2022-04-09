package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetTree;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class ActionHang extends PetAction {
    public ActionHang() {
        super(null, true);
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getHappyness() < 50) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
            return false;
        } else {

            Set<HabboItem> petTrees = pet.getRoom().getRoomSpecialTypes().getItemsOfType(InteractionPetTree.class);

            if (petTrees == null || petTrees.isEmpty()) {
                pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
                return false;
            }

            ArrayList<RoomTile> tileList = new ArrayList<>();

            for (HabboItem petTree : petTrees) {
                if (petTree == null || petTree.getRoomId() != pet.getRoom().getId()) continue;
                tileList.addAll(petTree.getOccupyingTiles(pet.getRoom().getLayout()));
            }

            if (!tileList.isEmpty()) {
                Collections.shuffle(tileList);
                RoomTile goal = tileList.get(0);
                pet.say(pet.getPetData().randomVocal(PetVocalsType.GENERIC_NEUTRAL));

                if (goal == null || goal.state == RoomTileState.BLOCKED) {
                    goal = pet.getRoomUnit().getClosestTile(tileList);
                }
                pet.setTask(PetTasks.HANG);
                if (goal.distance(pet.getRoomUnit().getCurrentLocation()) == 0) {
                    HabboItem tree = pet.getRoom().getItemsAt(goal).stream().filter(habboItem -> habboItem instanceof InteractionPetTree).findAny().orElse(null);
                    if (tree != null) {
                        try {
                            tree.onWalkOn(pet.getRoomUnit(), pet.getRoom(), null);
                        } catch (Exception ignored) {}
                    } else {
                        pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
                        return false;
                    }
                } else pet.getRoomUnit().setGoalLocation(goal);
                return true;
            } else {
                pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
                return false;
            }
        }
    }
}