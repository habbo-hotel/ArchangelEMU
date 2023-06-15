package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.users.WhisperMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectWhisper extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    protected String message = "";

    public WiredEffectWhisper(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectWhisper(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean saveData() throws WiredSaveException {
        String message = this.getWiredSettings().getStringParam();

        //TODO Removed ability to `If user has rights of SUPER WIRED can override these two lines`
        message = Emulator.getGameEnvironment().getWordFilter().filter(message, null);
        message = message.substring(0, Math.min(message.length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));

        int delay = this.getWiredSettings().getDelay();

        if(delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20))
            throw new WiredSaveException("Delay too long");

        this.message = message;
        this.getWiredSettings().setDelay(delay);
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (this.message.length() > 0) {
            if (roomUnit != null) {
                Habbo habbo = room.getHabbo(roomUnit);

                if (habbo != null) {
                    String msg = this.message.replace("%user%", habbo.getHabboInfo().getUsername()).replace("%online_count%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "").replace("%room_count%", Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "");
                    habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(msg, habbo, habbo, RoomChatMessageBubbles.WIRED)));
                    Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, roomUnit, room, new Object[]{ msg }));

                    if (habbo.getRoomUnit().isIdle()) {
                        habbo.getRoomUnit().getRoom().unIdle(habbo);
                    }
                    return true;
                }
            } else {
                for (Habbo h : room.getHabbos()) {
                    h.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(this.message.replace("%user%", h.getHabboInfo().getUsername()).replace("%online_count%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "").replace("%room_count%", Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + ""), h, h, RoomChatMessageBubbles.WIRED)));
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.message, this.getWiredSettings().getDelay()));
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
        String message;
        int delay;

        public JsonData(String message, int delay) {
            this.message = message;
            this.delay = delay;
        }
    }
}
