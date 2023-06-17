package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
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

    public WiredEffectBotGiveHandItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        List<Bot> bots = room.getBots(this.getWiredSettings().getStringParam());
        int itemId = this.getWiredSettings().getIntegerParams().get(PARAM_ITEM_ID);

        if (habbo != null && bots.size() == 1) {
            Bot bot = bots.get(0);

            List<Runnable> tasks = new ArrayList<>();
            tasks.add(new RoomUnitGiveHanditem(roomUnit, room, itemId));
            tasks.add(new RoomUnitGiveHanditem(bot.getRoomUnit(), room, 0));
            tasks.add(() -> {
                if(roomUnit.getRoom() != null && roomUnit.getRoom().getId() == room.getId() && roomUnit.getCurrentLocation().distance(bot.getRoomUnit().getCurrentLocation()) < 2) {
                    WiredHandler.handle(WiredTriggerType.BOT_REACHED_AVTR, bot.getRoomUnit(), room, new Object[]{});
                }
            });

            RoomTile tile = bot.getRoomUnit().getClosestAdjacentTile(roomUnit.getX(), roomUnit.getY(), true);

            if(tile != null) {
                bot.getRoomUnit().setGoalLocation(tile);
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
