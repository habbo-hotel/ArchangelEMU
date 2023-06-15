package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
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

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMuteHabbo extends InteractionWiredEffect {
    private static final WiredEffectType type = WiredEffectType.MUTE_TRIGGER;

    private int length = 5;
    private String message = "";

    public WiredEffectMuteHabbo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMuteHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean saveData() throws WiredSaveException {
        if(this.getWiredSettings().getIntegerParams().length < 1) throw new WiredSaveException("invalid data");

        this.length = this.getWiredSettings().getIntegerParams()[0];
        this.message = this.getWiredSettings().getStringParam();

        this.getWiredSettings().setDelay(this.getWiredSettings().getDelay());

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (roomUnit == null)
            return true;

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            if (room.hasRights(habbo))
                return false;

            room.muteHabbo(habbo, 60);

            habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(this.message.replace("%user%", habbo.getHabboInfo().getUsername()).replace("%online_count%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "").replace("%room_count%", Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + ""), habbo, habbo, RoomChatMessageBubbles.WIRED)));
        }

        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(
                this.getWiredSettings().getDelay(),
                this.length,
                this.message
        ));
    }

    @Override
    public void loadWiredSettings(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");

        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.getWiredSettings().setDelay(data.delay);
            this.length = data.length;
            this.message = data.message;
        } else {
            String[] data = wiredData.split("\t");

            if (data.length >= 3) {
                try {
                    this.getWiredSettings().setDelay(Integer.parseInt(data[0]));
                    this.length = Integer.parseInt(data[1]);
                    this.message = data[2];
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean requiresTriggeringUser() {
        return true;
    }

    static class JsonData {
        int delay;
        int length;
        String message;

        public JsonData(int delay, int length, String message) {
            this.delay = delay;
            this.length = length;
            this.message = message;
        }
    }
}
