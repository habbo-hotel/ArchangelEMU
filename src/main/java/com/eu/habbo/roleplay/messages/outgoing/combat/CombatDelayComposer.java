package com.eu.habbo.roleplay.messages.outgoing.combat;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CombatDelayComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.combatDelayComposer);
        this.response.appendBoolean(this.habbo.getHabboRoleplayStats().getCombatBlocked());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getCombatDelayRemaining());
        return this.response;
    }
}
