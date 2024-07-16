package com.eu.habbo.roleplay.messages.outgoing.police;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserArrestedComposer extends MessageComposer {
    private final Habbo arrestedHabbo;
    private final Habbo arrestedByHabbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userArrestedComposer);
        this.response.appendInt(this.arrestedHabbo.getHabboInfo().getId());
        this.response.appendString(this.arrestedHabbo.getHabboInfo().getUsername());
        this.response.appendString(this.arrestedHabbo.getHabboInfo().getLook());
        this.response.appendInt(this.arrestedByHabbo.getHabboInfo().getId());
        this.response.appendString(this.arrestedByHabbo.getHabboInfo().getUsername());
        this.response.appendString(this.arrestedByHabbo.getHabboInfo().getLook());
        return this.response;
    }
}
