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

            if (!this.getWiredSettings().getStringParam().isEmpty()) {
                habbo.getClient().sendResponse(new WhisperMessageComposer(new RoomChatMessage(this.getWiredSettings().getStringParam(), habbo, habbo, RoomChatMessageBubbles.ALERT)));
            }

            Emulator.getThreading().run(new RoomUnitKick(habbo, room, true), 2000);

            return true;
        }

        return false;
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.KICK_USER;
    }
}
