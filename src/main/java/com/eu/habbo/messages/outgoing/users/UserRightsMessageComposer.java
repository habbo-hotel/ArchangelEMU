package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UserRightsMessageComposer extends MessageComposer {
    private final int clubLevel;

    private final Habbo habbo;

    public UserRightsMessageComposer(Habbo habbo) {
        this.clubLevel = habbo.getHabboStats().hasActiveClub() ? 2 : 0;
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userRightsMessageComposer);
        this.response.appendInt(this.clubLevel);
        this.response.appendInt(this.habbo.getHabboInfo().getPermissionGroup().getLevel());
        this.response.appendBoolean(this.habbo.hasRight(Permission.ACC_AMBASSADOR));
        return this.response;
    }
}
