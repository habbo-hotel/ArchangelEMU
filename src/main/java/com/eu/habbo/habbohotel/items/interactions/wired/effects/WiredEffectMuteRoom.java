package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.constants.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.outgoing.rooms.users.WhisperMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMuteRoom extends InteractionWiredEffect {
    private final int PARAM_LENGTH = 0;

    public WiredEffectMuteRoom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMuteRoom(int id, HabboInfo ownerInfo, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, ownerInfo, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int length = this.getWiredSettings().getIntegerParams().get(PARAM_LENGTH);
        String message = this.getWiredSettings().getStringParam();

        if (roomUnit == null)
            return true;

        Habbo habbo = room.getRoomUnitManager().getHabboByRoomUnit(roomUnit);

        if (habbo != null) {
            if (room.getRoomRightsManager().hasRights(habbo))
                return false;

            room.muteHabbo(habbo, 60);

            habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(message.replace("%user%", habbo.getHabboInfo().getUsername()).replace("%online_count%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "").replace("%room_count%", Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + ""), habbo, habbo, RoomChatMessageBubbles.WIRED)));
        }

        return true;
    }


    @Override
    public WiredEffectType getType() {
        return WiredEffectType.MUTE_TRIGGER;
    }
}
