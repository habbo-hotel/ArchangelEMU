package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.RelationshipStatusInfoComposer;

public class GetRelationshipStatusInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        int userId = this.packet.readInt();
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

        if (habbo != null)
            this.client.sendResponse(new RelationshipStatusInfoComposer(habbo));
        else
            this.client.sendResponse(new RelationshipStatusInfoComposer(Messenger.getFriends(userId), userId));
    }
}
