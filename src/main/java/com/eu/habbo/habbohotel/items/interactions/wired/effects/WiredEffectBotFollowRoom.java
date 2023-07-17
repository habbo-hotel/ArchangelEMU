package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WiredEffectBotFollowRoom extends InteractionWiredEffect {
    public final int PARAM_MODE = 0;

    public WiredEffectBotFollowRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotFollowRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        List<Bot> bots = room.getRoomUnitManager().getBotsByName(this.getWiredSettings().getStringParam());

        if (habbo != null && bots.size() == 1) {
            Bot bot = bots.get(0);

            if (this.getWiredSettings().getIntegerParams().get(PARAM_MODE) == 1) {
                bot.startFollowingHabbo(habbo);
            } else {
                bot.stopFollowingHabbo();
            }

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
        return WiredEffectType.BOT_FOLLOW_AVATAR;
    }
}
