package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionNotFurniHaveFurni extends InteractionWiredCondition {
    public final int PARAM_ALL_FURNI = 0;

    public WiredConditionNotFurniHaveFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionNotFurniHaveFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        boolean allFurni = this.getWiredSettings().getIntegerParams().get(PARAM_ALL_FURNI) == 1;

        if(allFurni) {
            return this.getWiredSettings().getItems(room).stream().allMatch(item -> {
                double minZ = item.getZ() + Item.getCurrentHeight(item);
                THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                return occupiedTiles.stream().noneMatch(tile -> room.getItemsAt(tile).stream().anyMatch(matchedItem -> matchedItem != item && matchedItem.getZ() >= minZ));
            });
        }
        else {
            return this.getWiredSettings().getItems(room).stream().anyMatch(item -> {
                double minZ = item.getZ() + Item.getCurrentHeight(item);
                THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                return occupiedTiles.stream().noneMatch(tile -> room.getItemsAt(tile).stream().anyMatch(matchedItem -> matchedItem != item && matchedItem.getZ() >= minZ));
            });
        }
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.NOT_FURNI_HAVE_FURNI;
    }

    @Override
    public WiredConditionOperator operator() {
        // NICE TRY BUT THAT'S NOT HOW IT WORKS. NOTHING IN HABBO IS AN "OR" CONDITION - EVERY CONDITION MUST BE SUCCESS FOR THE STACK TO EXECUTE, BUT LET'S LEAVE IT IMPLEMENTED FOR PLUGINS TO USE.
        //return this.all ? WiredConditionOperator.AND : WiredConditionOperator.OR;
        return WiredConditionOperator.AND;
    }
}
