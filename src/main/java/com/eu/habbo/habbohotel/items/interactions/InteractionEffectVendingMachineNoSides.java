package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.HabboInfo;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionEffectVendingMachineNoSides extends InteractionVendingMachine {
    public InteractionEffectVendingMachineNoSides(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtraData("0");
    }

    public InteractionEffectVendingMachineNoSides(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.setExtraData("0");
    }

    @Override
    public void giveVendingMachineItem(Room room, RoomAvatar roomAvatar) {
        roomAvatar.giveEffect(this.getBaseItem().getRandomVendingItem(), 30);
    }

    @Override
    public THashSet<RoomTile> getActivatorTiles(Room room) {

        THashSet<RoomTile> tiles = new THashSet<>();
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                RoomTile tile = room.getLayout().getTile((short)(this.getCurrentPosition().getX() + x), (short)(this.getCurrentPosition().getY() + y));
                if(tile != null) {
                    tiles.add(tile);
                }
            }
        }

        return tiles;
    }
}