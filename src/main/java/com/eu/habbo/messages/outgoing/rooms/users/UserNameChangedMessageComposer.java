package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserNameChangedMessageComposer extends MessageComposer {

    private final int userId;
    private final int roomId;
    private final String name;

    public UserNameChangedMessageComposer(Habbo habbo) {
        this(habbo, false);
    }

    public UserNameChangedMessageComposer(Habbo habbo, boolean includePrefix) {
        this.userId = habbo.getHabboInfo().getId();
        this.roomId = habbo.getRoomUnit().getId();
        this.name = (includePrefix ? Room.PREFIX_FORMAT.replace("%color%", habbo.getHabboInfo().getPermissionGroup().getPrefixColor()).replace("%prefix%", habbo.getHabboInfo().getPermissionGroup().getPrefix()) : "") + habbo.getHabboInfo().getUsername();
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userNameChangedMessageComposer);
        this.response.appendInt(this.userId);
        this.response.appendInt(this.roomId);
        this.response.appendString(this.name);
        return this.response;
    }
}