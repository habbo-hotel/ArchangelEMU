package com.eu.habbo.roleplay.messages.outgoing.user;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserOnlineCountComposer extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userOnlineCountComposer);
        this.response.appendInt(Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().size());
        return this.response;
    }
}
