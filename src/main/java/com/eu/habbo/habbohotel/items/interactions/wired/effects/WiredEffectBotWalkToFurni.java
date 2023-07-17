package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectBotWalkToFurni extends InteractionWiredEffect {
    public WiredEffectBotWalkToFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotWalkToFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
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
        this.getWiredSettings().getItems(room).removeIf(item -> item == null || item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(this.getRoomId()).getHabboItem(item.getId()) == null);

        // Bots shouldn't walk to the tile they are already standing on
        List<RoomItem> possibleItems = this.getWiredSettings().getItems(room).stream()
                .filter(item -> !room.getBotsOnItem(item).contains(bot))
                .collect(Collectors.toList());

        // Get a random tile of possible tiles to walk to
        if (possibleItems.size() > 0) {
            RoomItem item = possibleItems.get(Emulator.getRandom().nextInt(possibleItems.size()));

            if (item.getRoomId() != 0) {
                Room room1 = bot.getRoom();
                if (item.getRoomId() == room1.getRoomInfo().getId()) {
                    bot.getRoomUnit().setGoalLocation(room.getLayout().getTile(item.getX(), item.getY()));
                }
            }
        }

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.BOT_MOVE;
    }
}
