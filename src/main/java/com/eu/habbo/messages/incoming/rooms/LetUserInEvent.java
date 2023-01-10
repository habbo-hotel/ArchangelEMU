package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.CloseConnectionMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.FlatAccessDeniedMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.FlatAccessibleMessageComposer;

public class LetUserInEvent extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null && this.client.getHabbo().getHabboInfo().getCurrentRoom().hasRights(this.client.getHabbo())) {
            String username = this.packet.readString();
            boolean accepted = this.packet.readBoolean();

            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);

            if (habbo != null && habbo.getHabboInfo().getRoomQueueId() == this.client.getHabbo().getHabboInfo().getCurrentRoom().getId()) {
                this.client.getHabbo().getHabboInfo().getCurrentRoom().removeFromQueue(habbo);

                if (accepted) {
                    habbo.getClient().sendResponse(new FlatAccessibleMessageComposer(""));
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, this.client.getHabbo().getHabboInfo().getCurrentRoom().getId(), "", true);
                } else {
                    habbo.getClient().sendResponse(new FlatAccessDeniedMessageComposer(""));
                    habbo.getClient().sendResponse(new CloseConnectionMessageComposer());
                }
                habbo.getHabboInfo().setRoomQueueId(0);
            }

        }
    }
}
