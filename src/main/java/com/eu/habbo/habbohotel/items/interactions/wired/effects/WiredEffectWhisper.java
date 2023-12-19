package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.chat.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.WhisperMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectWhisper extends InteractionWiredEffect {
    public WiredEffectWhisper(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectWhisper(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if(this.getWiredSettings().getStringParam().isEmpty()) {
            return false;
        }

        if (roomUnit != null) {
            Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

            if (habbo != null) {
                String msg = this.getWiredSettings().getStringParam().replace("%user%", habbo.getHabboInfo().getUsername()).replace("%online_count%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "").replace("%room_count%", Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "");
                habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(msg, habbo, habbo, RoomChatMessageBubbles.WIRED)));
                Emulator.getThreading().run(() -> WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, roomUnit, room, new Object[]{ msg }));

                if (habbo.getRoomUnit().isIdle()) {
                    habbo.getRoomUnit().unIdle();
                }
                return true;
            }
        } else {
            for (Habbo h : room.getRoomUnitManager().getCurrentHabbos().values()) {
                h.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(this.getWiredSettings().getStringParam().replace("%user%", h.getHabboInfo().getUsername()).replace("%online_count%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "").replace("%room_count%", Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + ""), h, h, RoomChatMessageBubbles.WIRED)));
            }

            return true;
        }

        return false;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.SHOW_MESSAGE;
    }
}
