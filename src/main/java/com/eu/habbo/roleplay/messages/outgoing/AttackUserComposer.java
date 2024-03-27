package com.eu.habbo.roleplay.messages.outgoing;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AttackUserComposer extends MessageComposer {
    private final Habbo targetedUser;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roleplayAttackUserComposer);

        this.response.appendInt(this.targetedUser.getHabboInfo().getId());

        return this.response;
    }
}