package com.eu.habbo.habbohotel.items.interactions.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMonsterPlantSeed extends RoomItem {
    public InteractionMonsterPlantSeed(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);

        if (this.getExtraData().isEmpty()) {
            this.setExtraData("" + randomRarityLevel());
            this.setSqlUpdateNeeded(true);
        }
    }

    public InteractionMonsterPlantSeed(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);

        if (this.getExtraData().isEmpty()) {
            this.setExtraData("" + randomRarityLevel());
            this.setSqlUpdateNeeded(true);
        }
    }

    public static int randomGoldenRarityLevel() {
        int number = Emulator.getRandom().nextInt(66);
        int count = 0;
        for (int i = 8; i < 11; i++) {
            count += 11 - i;
            if (number <= count) {
                return i;
            }
        }
        return 10;
    }

    public static int randomRarityLevel() {
        int number = Emulator.getRandom().nextInt(66);
        int count = 0;
        for (int i = 1; i < 11; i++) {
            count += 11 - i;
            if (number <= count) {
                return i;
            }
        }
        return 10;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) {
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt(1 + (this.isLimited() ? 256 : 0));
        serverMessage.appendInt(1);
        serverMessage.appendString("rarity");
        serverMessage.appendString(this.getExtraData());

        super.serializeExtradata(serverMessage);
    }
}
