package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WiredConditionFurniHaveFurni extends InteractionWiredCondition {
    public final int PARAM_ALL_FURNI = 0;

    public WiredConditionFurniHaveFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionFurniHaveFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        boolean allFurni = this.getWiredSettings().getIntegerParams().get(PARAM_ALL_FURNI) == 1;

        if (allFurni) {
            return this.getWiredSettings().getItems(room).stream().allMatch(item -> {
                double minZ = item.getZ() + Item.getCurrentHeight(item);
                THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                return occupiedTiles.stream().anyMatch(tile -> room.getItemsAt(tile).stream().anyMatch(matchedItem -> matchedItem != item && matchedItem.getZ() >= minZ));
            });
        } else {
            return this.getWiredSettings().getItems(room).stream().anyMatch(item -> {
                double minZ = item.getZ() + Item.getCurrentHeight(item);
                THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                return occupiedTiles.stream().anyMatch(tile -> room.getItemsAt(tile).stream().anyMatch(matchedItem -> matchedItem != item && matchedItem.getZ() >= minZ));
            });
        }
    }

    @Override
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.FURNI_HAS_FURNI;
    }
}
