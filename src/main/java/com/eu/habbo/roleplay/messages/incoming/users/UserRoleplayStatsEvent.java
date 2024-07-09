package com.eu.habbo.roleplay.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;

public class UserRoleplayStatsEvent extends MessageHandler {
    @Override
    public void handle() {
        Integer userID = this.packet.readInt();

        if (userID == null) {
            return;
        }

        Habbo targetedUser = Emulator.getGameEnvironment().getHabboManager().getHabbo(userID);

        if (targetedUser == null) {
            return;
        }

        this.client.sendResponse(new UserRoleplayStatsChangeComposer(targetedUser));

    }
}