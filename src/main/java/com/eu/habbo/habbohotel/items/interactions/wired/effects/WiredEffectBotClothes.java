package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WiredEffectBotClothes extends InteractionWiredEffect {
    public WiredEffectBotClothes(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotClothes(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        String[] stringParams = this.getWiredSettings().getStringParam().split("\t");

        String botName = stringParams[0].substring(0, Math.min(stringParams[0].length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        String botLook = stringParams[1];

        List<Bot> bots = room.getRoomUnitManager().getBotsByName(botName);

        if(bots.size() == 0) {
            return false;
        }

        Bot bot = bots.get(0);
        bot.setFigure(botLook);

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.BOT_CLOTHES;
    }
}
