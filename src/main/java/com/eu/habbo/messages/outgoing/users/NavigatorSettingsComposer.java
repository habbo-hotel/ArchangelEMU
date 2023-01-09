package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NavigatorSettingsComposer extends MessageComposer {
    private final int homeRoom;
    private final int roomToEnter;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.navigatorSettingsComposer);
        this.response.appendInt(this.homeRoom);
        this.response.appendInt(this.roomToEnter);
        return this.response;
    }
}
