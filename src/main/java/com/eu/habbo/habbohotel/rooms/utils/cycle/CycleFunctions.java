package com.eu.habbo.habbohotel.rooms.utils.cycle;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.constants.RoomConfiguration;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.SleepMessageComposer;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;

import java.util.ArrayList;

public class CycleFunctions {

    public static void cycleIdle(Room room, Habbo habbo, ArrayList<Habbo> toKick) {
        if (!habbo.getRoomUnit().isIdle()) {
            habbo.getRoomUnit().incrementIdleTicks();

            if (habbo.getRoomUnit().isIdle()) {
                boolean danceIsNone = (habbo.getRoomUnit().getDanceType() == DanceType.NONE);
                if (danceIsNone)
                    room.sendComposer(new SleepMessageComposer(habbo.getRoomUnit()).compose());
                if (danceIsNone && !Emulator.getConfig().getBoolean("hotel.roomuser.idle.not_dancing.ignore.wired_idle"))
                    WiredHandler.handle(WiredTriggerType.IDLES, habbo.getRoomUnit(), room, new Object[]{habbo});
            }
        } else {
            habbo.getRoomUnit().incrementIdleTicks();

            if (!room.getRoomInfo().isRoomOwner(habbo) && habbo.getRoomUnit().getIdleTicks() >= RoomConfiguration.IDLE_CYCLES_KICK) {
                UserExitRoomEvent event = new UserExitRoomEvent(habbo, UserExitRoomEvent.UserExitRoomReason.KICKED_IDLE);
                Emulator.getPluginManager().fireEvent(event);

                if (!event.isCancelled()) {
                    toKick.add(habbo);
                }
            }
        }
    }

}
