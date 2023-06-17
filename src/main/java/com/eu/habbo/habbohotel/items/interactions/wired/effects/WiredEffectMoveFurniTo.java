package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMoveFurniTo extends InteractionWiredEffect {
    public final int PARAM_DIRECTION = 0;
    public final int PARAM_SPACING = 1;

    public WiredEffectMoveFurniTo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniTo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        if(stuff == null || stuff.length == 0) {
            return false;
        }

        int direction = this.getWiredSettings().getIntegerParams().get(PARAM_DIRECTION);
        int spacing = this.getWiredSettings().getIntegerParams().get(PARAM_SPACING);

        for (Object object : stuff) {
            if (object instanceof HabboItem) {
                int randomItemIndex = Emulator.getRandom().nextInt(this.getWiredSettings().getItemIds().size());

                HabboItem[] items = this.getWiredSettings().getItems(room).toArray(new HabboItem[this.getWiredSettings().getItemIds().size()]);

                HabboItem randomItem = items[randomItemIndex];

                if (randomItem != null) {
                    int indexOffset = 0;

                    RoomTile objectTile = room.getLayout().getTile(randomItem.getX(), randomItem.getY());

                    if (objectTile != null) {
                        THashSet<RoomTile> refreshTiles = room.getLayout().getTilesAt(room.getLayout().getTile(((HabboItem) object).getX(), ((HabboItem) object).getY()), ((HabboItem) object).getBaseItem().getWidth(), ((HabboItem) object).getBaseItem().getLength(), ((HabboItem) object).getRotation());

                        RoomTile tile = room.getLayout().getTileInFront(objectTile, direction, indexOffset);
                        if (tile == null || !tile.getAllowStack()) {
                            indexOffset = 0;
                            tile = room.getLayout().getTileInFront(objectTile, direction, indexOffset);
                        }

                        room.sendComposer(new FloorItemOnRollerComposer((HabboItem) object, null, tile, tile.getStackHeight() - ((HabboItem) object).getZ(), room).compose());
                        refreshTiles.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(((HabboItem) object).getX(), ((HabboItem) object).getY()), ((HabboItem) object).getBaseItem().getWidth(), ((HabboItem) object).getBaseItem().getLength(), ((HabboItem) object).getRotation()));
                        room.updateTiles(refreshTiles);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
            this.getWiredSettings().getIntegerParams().add(1);
        }
    }

    @Override
    protected long requiredCooldown() {
        return 495;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.MOVE_FURNI_TO;
    }
}