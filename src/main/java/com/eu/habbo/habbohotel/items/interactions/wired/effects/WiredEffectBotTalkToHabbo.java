package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WiredEffectBotTalkToHabbo extends InteractionWiredEffect {
    public final int PARAM_MODE = 0;

    public WiredEffectBotTalkToHabbo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotTalkToHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        String[] stringParams = this.getWiredSettings().getStringParam().split("\t");

        String botName = stringParams[0].substring(0, Math.min(stringParams[0].length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        String message = stringParams[1].substring(0, Math.min(stringParams[1].length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            message = message.replace(Emulator.getTexts().getValue("wired.variable.username", "%username%"), habbo.getHabboInfo().getUsername())
                    .replace(Emulator.getTexts().getValue("wired.variable.credits", "%credits%"), habbo.getHabboInfo().getCredits() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.pixels", "%pixels%"), habbo.getHabboInfo().getPixels() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.points", "%points%"), habbo.getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type")) + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.owner", "%owner%"), room.getOwnerName())
                    .replace(Emulator.getTexts().getValue("wired.variable.item_count", "%item_count%"), room.itemCount() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.name", "%name%"), botName)
                    .replace(Emulator.getTexts().getValue("wired.variable.roomname", "%roomname%"), room.getName())
                    .replace(Emulator.getTexts().getValue("wired.variable.user_count", "%user_count%"), room.getUserCount() + "");

            List<Bot> bots = room.getBots(botName);

            if (bots.size() != 1) {
                return false;
            }

            Bot bot = bots.get(0);

            if(!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, bot.getRoomUnit(), room, new Object[]{ message })) {
                if (this.getWiredSettings().getIntegerParams().get(PARAM_MODE) == 1) {
                    bot.whisper(message, habbo);
                } else {
                    bot.talk(habbo.getHabboInfo().getUsername() + ": " + message);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void loadDefaultParams() {
        if(this.getWiredSettings().getIntegerParams().size() == 0) {
            this.getWiredSettings().getIntegerParams().add(0);
        }
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.BOT_TALK_TO_AVATAR;
    }
}
