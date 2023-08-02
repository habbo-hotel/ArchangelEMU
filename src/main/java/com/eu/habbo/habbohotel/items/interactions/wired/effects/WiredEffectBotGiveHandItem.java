package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectBotGiveHandItem extends InteractionWiredEffect {
    public final int PARAM_ITEM_ID = 0;
    public WiredEffectBotGiveHandItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotGiveHandItem(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(!(roomUnit instanceof RoomAvatar roomAvatar)) {
            return false;
        }

        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomAvatar);
        List<Bot> bots = room.getRoomUnitManager().getBotsByName(this.getWiredSettings().getStringParam());
        int itemId = this.getWiredSettings().getIntegerParams().get(PARAM_ITEM_ID);

        if (habbo != null && bots.size() == 1) {
            Bot bot = bots.get(0);

            List<Runnable> tasks = new ArrayList<>();
            tasks.add(new RoomUnitGiveHanditem(roomAvatar, room, itemId));
            tasks.add(new RoomUnitGiveHanditem(bot.getRoomUnit(), room, 0));
            tasks.add(() -> {
                if(roomAvatar.getRoom() != null) {
                    if (roomAvatar.getRoom().getRoomInfo().getId() == room.getRoomInfo().getId() && roomAvatar.getCurrentPosition().distance(bot.getRoomUnit().getCurrentPosition()) < 2) {
                        WiredHandler.handle(WiredTriggerType.BOT_REACHED_AVTR, bot.getRoomUnit(), room, new Object[]{});
                    }
                }
            });

            RoomTile tile = bot.getRoomUnit().getClosestAdjacentTile(roomAvatar.getCurrentPosition().getX(), roomAvatar.getCurrentPosition().getY(), true);

            if(tile != null) {
                bot.getRoomUnit().walkTo(tile);
            }

            Emulator.getThreading().run(new RoomUnitGiveHanditem(bot.getRoomUnit(), room, itemId));
            Emulator.getThreading().run(new RoomUnitWalkToLocation(bot.getRoomUnit(), tile, room, tasks, tasks));

            return true;
        }

        return false;
    }

    @Override
    public void loadDefaultIntegerParams() {
        if(this.getWiredSettings().getIntegerParams().isEmpty()) {
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.BOT_GIVE_HANDITEM;
    }
}
