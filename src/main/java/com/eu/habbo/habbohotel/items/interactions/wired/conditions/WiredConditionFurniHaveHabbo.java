package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WiredConditionFurniHaveHabbo extends InteractionWiredCondition {
    public WiredConditionFurniHaveHabbo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionFurniHaveHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        Collection<Habbo> habbos = room.getHabbos();
        Collection<Bot> bots = room.getCurrentBots().valueCollection();
        Collection<Pet> pets = room.getCurrentPets().valueCollection();

        return this.getWiredSettings().getItems(room).stream().allMatch(item -> {
            THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            return habbos.stream().anyMatch(character -> occupiedTiles.contains(character.getRoomUnit().getCurrentLocation())) ||
                    bots.stream().anyMatch(character -> occupiedTiles.contains(character.getRoomUnit().getCurrentLocation())) ||
                    pets.stream().anyMatch(character -> occupiedTiles.contains(character.getRoomUnit().getCurrentLocation()));
        });
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.FURNI_HAVE_HABBO;
    }
}
