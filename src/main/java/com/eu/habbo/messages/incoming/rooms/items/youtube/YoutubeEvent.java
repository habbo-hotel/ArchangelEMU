package com.eu.habbo.messages.incoming.rooms.items.youtube;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public abstract class YoutubeEvent extends MessageHandler {

    protected boolean validate(Habbo habbo) {
        if (habbo == null) {
            return false;
        }

        Room room = habbo.getHabboInfo().getCurrentRoom();

        if (room == null) {
            return false;
        }
        if (!room.isOwner(habbo) && !habbo.hasRight(Permission.ACC_ANYROOMOWNER)) {
            return false;
        }

        return true;
    }
}
