package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangeUserNameResultMessageEvent extends MessageComposer {
    private final Habbo habbo;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.changeUserNameResultMessageEvent);
        this.response.appendInt(0);
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendInt(0);
        return this.response;
    }
}
