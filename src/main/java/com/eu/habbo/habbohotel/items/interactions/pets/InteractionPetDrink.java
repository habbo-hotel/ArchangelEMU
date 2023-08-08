package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.threading.runnables.PetClearPosture;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InteractionPetDrink extends InteractionDefault {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionPetDrink.class);


    public InteractionPetDrink(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionPetDrink(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canToggle(Habbo habbo, Room room) {
        return RoomLayout.tilesAdjecent(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()), habbo.getRoomUnit().getCurrentPosition());
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
        if (client == null)
            return;

        if (!this.canToggle(client.getHabbo(), room)) {
            RoomTile closestTile = null;
            for (RoomTile tile : room.getLayout().getTilesAround(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()))) {
                if (tile.isWalkable() && (closestTile == null || closestTile.distance(client.getHabbo().getRoomUnit().getCurrentPosition()) > tile.distance(client.getHabbo().getRoomUnit().getCurrentPosition()))) {
                    closestTile = tile;
                }
            }

            if (closestTile != null && !closestTile.equals(client.getHabbo().getRoomUnit().getCurrentPosition())) {
                List<Runnable> onSuccess = new ArrayList<>();
                onSuccess.add(() -> this.change(room, this.getBaseItem().getStateCount() - 1));

                client.getHabbo().getRoomUnit().walkTo(closestTile);
                Emulator.getThreading().run(new RoomUnitWalkToLocation(client.getHabbo().getRoomUnit(), closestTile, room, onSuccess, new ArrayList<>()));
            }
        }

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);

        if (this.getExtraData() == null || this.getExtraData().isEmpty())
            this.setExtraData("0");

        Pet pet = room.getRoomUnitManager().getPetByRoomUnit(roomUnit);

        if (pet != null && pet.getPetData().haveDrinkItem(this) && pet.levelThirst >= 35) {
            pet.clearPosture();
            pet.getRoomUnit().walkTo(room.getLayout().getTile(this.getCurrentPosition().getX(), this.getCurrentPosition().getY()));
            pet.getRoomUnit().setRotation(RoomRotation.values()[this.getRotation()]);
            pet.getRoomUnit().clearStatuses();
            pet.getRoomUnit().addStatus(RoomUnitStatus.EAT, pet.getRoomUnit().getCurrentPosition().getStackHeight() + "");
            pet.setPacketUpdate(true);

            Emulator.getThreading().run(() -> {
                pet.addThirst(-75);
                this.change(room, -1);
                pet.getRoomUnit().clearStatuses();
                new PetClearPosture(pet, RoomUnitStatus.EAT, null, true);
                pet.setPacketUpdate(true);
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

        if (this.getExtraData() == null || this.getExtraData().isEmpty()) {
            this.setExtraData("0");
        }

        try {
            state = Integer.parseInt(this.getExtraData());
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

        this.setExtraData(state + "");
        this.setSqlUpdateNeeded(true);
        room.updateItemState(this);
    }

}
