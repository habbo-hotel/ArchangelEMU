package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.ExtendedProfileMessageComposer;

public class GetExtendedProfileEvent extends MessageHandler {
    @Override
    public void handle() {
        int habboId = this.packet.readInt();
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(habboId);

        if (habbo != null)
            this.client.sendResponse(new ExtendedProfileMessageComposer(habbo, this.client));
        else
            this.client.sendResponse(new ExtendedProfileMessageComposer(HabboManager.getOfflineHabboInfo(habboId), this.client));
    }
}
