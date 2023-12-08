package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.RoomItemManager;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionNest extends RoomItem {
    public InteractionNest(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionNest(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void removeThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getNests().remove(getId());
    }

    @Override
    public void addThisItem(RoomItemManager roomItemManager) {
        roomItemManager.getNests().put(getId(), this);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        Pet pet = room.getRoomUnitManager().getPetByRoomUnit(roomUnit);

        if (pet == null)
            return;

        if (pet instanceof RideablePet && ((RideablePet) pet).getRider() != null)
            return;

        if (!pet.getPetData().haveNest(this))
            return;

        if (pet.getEnergy() > 85)
            return;

        pet.setTask(PetTasks.NEST);
        pet.getRoomUnit().walkTo(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
        pet.getRoomUnit().clearStatuses();
        pet.getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
        pet.getRoomUnit().addStatus(RoomUnitStatus.LAY, room.getStackHeight(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), false) + "");
        room.sendComposer(new UserUpdateComposer(roomUnit).compose());
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
    }
}
