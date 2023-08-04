package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.units.type.Avatar;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Collection;
import java.util.List;

public class RoomUsersComposer extends MessageComposer {
    private final Collection<? extends Avatar> avatars;

    public RoomUsersComposer(Avatar avatar) {
        this.avatars = List.of(avatar);
    }

    public RoomUsersComposer(Collection<? extends Avatar> avatars) {
        this.avatars = avatars;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.usersComposer);
        if (this.avatars != null) {
            this.response.appendInt(this.avatars.size());
            for (Avatar avatar : this.avatars) {
                if (avatar != null) {
                    avatar.serialize(this.response);
                }
            }
        }

        return this.response;
    }
}