package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomBot;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import com.eu.habbo.threading.runnables.RoomUnitTeleport;
import com.eu.habbo.threading.runnables.SendRoomUnitEffectComposer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class WiredEffectBotTeleport extends InteractionWiredEffect {
    public WiredEffectBotTeleport(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotTeleport(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getItemIds().isEmpty()) {
            return false;
        }

        String botName = this.getWiredSettings().getStringParam();
        List<Bot> bots = room.getRoomUnitManager().getBotsByName(botName);

        if (bots.size() == 0) {
            return false;
        }

        Bot bot = bots.get(0);

        int i = Emulator.getRandom().nextInt(this.getWiredSettings().getItemIds().size()) + 1;
        int j = 1;

        for (RoomItem item : this.getWiredSettings().getItems(room)) {
            if (item.getRoomId() != 0) {
                Room room1 = bot.getRoom();
                if (item.getRoomId() == room1.getRoomInfo().getId()) {
                    if (i == j) {
                        teleportUnitToTile(bot.getRoomUnit(), room.getLayout().getTile(item.getX(), item.getY()));
                        return true;
                    } else {
                        j++;
                    }
                }
            }
        }

        return true;
    }

    public static void teleportUnitToTile(RoomUnit roomUnit, RoomTile tile) {
        if (roomUnit == null || tile == null || roomUnit.isWiredTeleporting() || !(roomUnit instanceof RoomBot roomBot))
            return;

        Room room = roomBot.getRoom();

        if (room == null) {
            return;
        }

        room.sendComposer(new AvatarEffectMessageComposer(roomBot, 4).compose());
        Emulator.getThreading().run(new SendRoomUnitEffectComposer(room, roomBot), (long) WiredHandler.TELEPORT_DELAY + 1000);

        if (tile == roomBot.getCurrentPosition()) {
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

        Emulator.getThreading().run(() -> roomBot.setWiredTeleporting(true), Math.max(0, WiredHandler.TELEPORT_DELAY - 500));
        Emulator.getThreading().run(new RoomUnitTeleport(roomBot, room, tile.getX(), tile.getY(), tile.getStackHeight() + (tile.getState() == RoomTileState.SIT ? -0.5 : 0), roomBot.getEffectId()), WiredHandler.TELEPORT_DELAY);
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.BOT_TELEPORT;
    }
}
