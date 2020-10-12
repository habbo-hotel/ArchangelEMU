package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class WiredEffectBotTalk extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.BOT_TALK;

    private int mode;
    private String botName = "";
    private String message = "";

    public WiredEffectBotTalk(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotTalk(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.botName + "" + ((char) 9) + "" + this.message);
        message.appendInt(1);
        message.appendInt(this.mode);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) throws WiredSaveException {
        packet.readInt();

        int mode = packet.readInt();

        if(mode != 0 && mode != 1)
            throw new WiredSaveException("Mode is invalid");

        String dataString = packet.readString();

        String splitBy = "\t";
        if(!dataString.contains(splitBy))
            throw new WiredSaveException("Malformed data string");

        String[] data = dataString.split(Pattern.quote(splitBy));

        if (data.length != 2)
            throw new WiredSaveException("Malformed data string. Invalid data length");

        packet.readInt();
        int delay = packet.readInt();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.setDelay(delay);
        this.botName = data[0].substring(0, Math.min(data[0].length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        this.message = data[1].substring(0, Math.min(data[1].length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        this.mode = mode;

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        String message = this.message;

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            message = message.replace(Emulator.getTexts().getValue("wired.variable.username"), habbo.getHabboInfo().getUsername())
                    .replace(Emulator.getTexts().getValue("wired.variable.credits"), habbo.getHabboInfo().getCredits() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.pixels"), habbo.getHabboInfo().getPixels() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.points"), habbo.getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type")) + "");
        }

        List<Bot> bots = room.getBots(this.botName);

        if (bots.size() == 1) {
            Bot bot = bots.get(0);

            if(!WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, bot.getRoomUnit(), room, new Object[]{ message })) {
                if (this.mode == 1) {
                    bot.shout(message);
                } else {
                    bot.talk(message);
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData() {
        return this.getDelay() + "" + ((char) 9) + "" + this.mode + "" + ((char) 9) + "" + this.botName + "" + ((char) 9) + "" + this.message;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String d = set.getString("wired_data");
        String[] data = d.split(((char) 9) + "");

        if (data.length == 4) {
            this.setDelay(Integer.valueOf(data[0]));
            this.mode = data[1].equalsIgnoreCase("1") ? 1 : 0;
            this.botName = data[2];
            this.message = data[3];
        }
    }

    @Override
    public void onPickUp() {
        this.mode = 0;
        this.botName = "";
        this.message = "";
        this.setDelay(0);
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getBotName() {
        return this.botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected long requiredCooldown() {
        return 500;
    }
}
