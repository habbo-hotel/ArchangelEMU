package com.eu.habbo.roleplay.messages.outgoing.combat;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDiedComposer extends MessageComposer {
    private final Habbo killedHabbo;
    private final Habbo killedByHabbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userDiedComposer);
        this.response.appendInt(this.killedHabbo.getHabboInfo().getId());
        this.response.appendString(this.killedHabbo.getHabboInfo().getUsername());
        this.response.appendString(this.killedHabbo.getHabboInfo().getLook());
        this.response.appendInt(this.killedByHabbo.getHabboInfo().getId());
        this.response.appendString(this.killedByHabbo.getHabboInfo().getUsername());
        this.response.appendString(this.killedByHabbo.getHabboInfo().getLook());
        return this.response;
    }
}
