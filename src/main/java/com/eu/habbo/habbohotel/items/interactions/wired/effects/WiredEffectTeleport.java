package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomHabbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.threading.runnables.RoomUnitTeleport;
import com.eu.habbo.threading.runnables.SendRoomUnitEffectComposer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WiredEffectTeleport extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.TELEPORT;

    protected List<RoomItem> items;

    public WiredEffectTeleport(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.items = new ArrayList<>();
    }

    public WiredEffectTeleport(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
        this.items = new ArrayList<>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        int randomItemIndex = Emulator.getRandom().nextInt(this.getWiredSettings().getItemIds().size());

        RoomItem[] items = this.getWiredSettings().getItems(room).toArray(new RoomItem[this.getWiredSettings().getItemIds().size()]);

        RoomItem randomItem = items[randomItemIndex];

        teleportUnitToTile(roomUnit, room.getLayout().getTile(randomItem.getCurrentPosition().getX(), randomItem.getCurrentPosition().getY()));

        return true;
    }

    public static void teleportUnitToTile(RoomUnit roomUnit, RoomTile tile) {
        if (roomUnit == null || tile == null || roomUnit.isWiredTeleporting() || !(roomUnit instanceof RoomHabbo roomHabbo))
            return;

        Room room = roomHabbo.getRoom();

        if (room == null) {
            return;
        }

        // makes a temporary effect

        roomHabbo.unIdle();
        room.sendComposer(new AvatarEffectMessageComposer(roomHabbo, 4).compose());
        Emulator.getThreading().run(new SendRoomUnitEffectComposer(room, roomHabbo), (long) WiredHandler.TELEPORT_DELAY + 1000);

        if (tile == roomHabbo.getCurrentPosition()) {
            return;
        }

        if (tile.getState() == RoomTileState.INVALID || tile.getState() == RoomTileState.BLOCKED) {
            RoomTile alternativeTile = null;
            List<RoomTile> optionalTiles = room.getLayout().getTilesAround(tile);

            Collections.reverse(optionalTiles);
            for (RoomTile optionalTile : optionalTiles) {
                if (optionalTile.getState() != RoomTileState.INVALID && optionalTile.getState() != RoomTileState.BLOCKED) {
                    alternativeTile = optionalTile;
                    break;
                }
            }

            if (alternativeTile != null) {
                tile = alternativeTile;
            }
        }

        Emulator.getThreading().run(() -> { roomHabbo.setWiredTeleporting(true); }, Math.max(0, WiredHandler.TELEPORT_DELAY - 500));
        Emulator.getThreading().run(new RoomUnitTeleport(roomHabbo, room, tile.getX(), tile.getY(), tile.getStackHeight() + (tile.getState() == RoomTileState.SIT ? -0.5 : 0), roomHabbo.getEffectId()), WiredHandler.TELEPORT_DELAY);
    }

    @Override
    protected long requiredCooldown() {
        return 50L;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.TELEPORT;
    }
}
