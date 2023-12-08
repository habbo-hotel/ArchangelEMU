package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.RoomItemManager;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.threading.runnables.PetEatAction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetFood extends InteractionDefault {
    public InteractionPetFood(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPetFood(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if (this.getExtraData().length() == 0)
            this.setExtraData("0");

        Pet pet = room.getRoomUnitManager().getPetByRoomUnit(roomUnit);

        if (pet != null) {
            if (pet.getPetData().haveFoodItem(this)) {
                if (pet.levelHunger >= 35) {
                    pet.setTask(PetTasks.EAT);
                    pet.getRoomUnit().walkTo(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
                    pet.getRoomUnit().setRotation(RoomRotation.values()[this.getRotation()]);
                    pet.getRoomUnit().clearStatuses();
                    pet.getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
                    pet.getRoomUnit().addStatus(RoomUnitStatus.EAT, "0");
                    room.sendComposer(new UserUpdateComposer(roomUnit).compose());
                    Emulator.getThreading().run(new PetEatAction(pet, this));
                }
            }
        }
    }

    @Override
    public void removeThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getPetFoods().remove(getId());
    }

    @Override
    public void addThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getPetFoods().put(getId(), this);
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }
}
