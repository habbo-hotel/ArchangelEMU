package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.users.WhisperMessageComposer;
import com.eu.habbo.threading.runnables.RoomUnitKick;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectKickHabbo extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.KICK_USER;

    private String message = "";

    public WiredEffectKickHabbo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectKickHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (room == null)
            return false;

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            if (habbo.hasRight(Permission.ACC_UNKICKABLE)) {
                habbo.whisper(Emulator.getTexts().getValue("hotel.wired.kickexception.unkickable"));
                return true;
            }

            if (habbo.getHabboInfo().getId() == room.getOwnerId()) {
                habbo.whisper(Emulator.getTexts().getValue("hotel.wired.kickexception.owner"));
                return true;
            }

            room.giveEffect(habbo, 4, 2);

            if (!this.message.isEmpty())
                habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(this.message, habbo, habbo, RoomChatMessageBubbles.ALERT)));

            Emulator.getThreading().run(new RoomUnitKick(habbo, room, true), 2000);

            return true;
        }

        return false;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.message, this.getWiredSettings().getDelay()));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if(wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.getWiredSettings().setDelay(data.delay);
            this.message = data.message;
        }
        else {
            try {
                String[] data = set.getString("wired_data").split("\t");

                if (data.length >= 1) {
                    this.getWiredSettings().setDelay(Integer.parseInt(data[0]));

                    if (data.length >= 2) {
                        this.message = data[1];
                    }
                }
            } catch (Exception e) {
                this.message = "";
                this.getWiredSettings().setDelay(0);
            }

            this.needsUpdate(true);
        }
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean saveData() throws WiredSaveException {
        String message = this.getWiredSettings().getStringParam();
        int delay = this.getWiredSettings().getDelay();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.message = message.substring(0, Math.min(message.length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        this.getWiredSettings().setDelay(delay);

        return true;
    }

    @Override
    public boolean requiresTriggeringUser() {
        return true;
    }

    static class JsonData {
        String message;
        int delay;

        public JsonData(String message, int delay) {
            this.message = message;
            this.delay = delay;
        }
    }
}
