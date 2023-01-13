package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.ExpressionMessageComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;

public class AvatarExpressionEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room == null) {
            return;
        }

        Habbo habbo = this.client.getHabbo();

        if (this.client.getHabbo().getRoomUnit().getCacheable().get("control") != null) {
            habbo = (Habbo) this.client.getHabbo().getRoomUnit().getCacheable().get("control");

            if (habbo.getHabboInfo().getCurrentRoom() != room) {
                habbo.getRoomUnit().getCacheable().remove("controller");
                this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                habbo = this.client.getHabbo();
            }
        }

        int action = this.packet.readInt();

        if (action == 5) {
            UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.ACTION, true);
            Emulator.getPluginManager().fireEvent(event);

            if (!event.isCancelled()) {
                if (event.isIdle()) {
                    room.idle(habbo);
                } else {
                    room.unIdle(habbo);
                }
            }
        } else {
            UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.ACTION, false);
            Emulator.getPluginManager().fireEvent(event);

            if (!event.isCancelled() && !event.isIdle()) {
                room.unIdle(habbo);
            }

        }

        room.sendComposer(new ExpressionMessageComposer(habbo.getRoomUnit(), RoomUserAction.fromValue(action)).compose());
    }
}
