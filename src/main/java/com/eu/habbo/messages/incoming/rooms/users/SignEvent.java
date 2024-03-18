package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionVoteCounter;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.users.UserSignEvent;

public class SignEvent extends MessageHandler {
    @Override
    public void handle() {
        int signId = this.packet.readInt();

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room == null) {
            return;
        }

        UserSignEvent event = new UserSignEvent(this.client.getHabbo(), signId);

        if (!Emulator.getPluginManager().fireEvent(event).isCancelled()) {
            this.client.getHabbo().getRoomUnit().addStatus(RoomUnitStatus.SIGN, String.valueOf(event.getSign()));
            this.client.getHabbo().getRoomUnit().unIdle();

            if(signId <= 10) {
                int userId = this.client.getHabbo().getHabboInfo().getId();
                for (RoomItem item : room.getRoomItemManager().getFloorItems().values()) {
                    if (item instanceof InteractionVoteCounter) {
                        ((InteractionVoteCounter)item).vote(room, userId, signId);
                    }
                }
            }
        }
    }
}
