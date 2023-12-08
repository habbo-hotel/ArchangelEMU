package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class WiredConditionFurniHaveRoom extends InteractionWiredCondition {
    public WiredConditionFurniHaveRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionFurniHaveRoom(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (this.getWiredSettings().getItemIds().isEmpty()) {
            return true;
        }

        Collection<Habbo> habbos = room.getRoomUnitManager().getCurrentHabbos().values();
        Collection<Bot> bots = room.getRoomUnitManager().getRoomBotManager().getCurrentBots().values();
        Collection<Pet> pets = room.getRoomUnitManager().getCurrentPets().values();

        return this.getWiredSettings().getItems(room).stream().allMatch(item -> {
            THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getCurrentPosition().getX(), item.getCurrentPosition().getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            return habbos.stream().anyMatch(character -> occupiedTiles.contains(character.getRoomUnit().getCurrentPosition())) ||
                    bots.stream().anyMatch(character -> occupiedTiles.contains(character.getRoomUnit().getCurrentPosition())) ||
                    pets.stream().anyMatch(character -> occupiedTiles.contains(character.getRoomUnit().getCurrentPosition()));
        });
    }

    @Override
    public WiredConditionType getType() {
        return WiredConditionType.FURNI_HAVE_HABBO;
    }
}
