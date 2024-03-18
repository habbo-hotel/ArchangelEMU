package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NewFriendRequestComposer extends MessageComposer {
    private final HabboInfo habbo;

    public NewFriendRequestComposer(HabboInfo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.newFriendRequestComposer);

        this.response.appendInt(this.habbo.getId());
        this.response.appendString(this.habbo.getUsername());
        this.response.appendString(this.habbo.getLook());

        return this.response;
    }
}
