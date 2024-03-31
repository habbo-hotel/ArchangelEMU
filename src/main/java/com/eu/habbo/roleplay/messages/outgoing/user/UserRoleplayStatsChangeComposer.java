package com.eu.habbo.roleplay.messages.outgoing.user;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corps.CorporationsShiftManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRoleplayStatsChangeComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userRoleplayStatsChangeComposer);
        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendString(this.habbo.getHabboInfo().getLook());
        this.response.appendInt(this.habbo.getHabboInfo().getCredits());
        this.response.appendInt(0); // TODO: Bank
        this.response.appendBoolean(this.habbo.getHabboRoleplayStats().isDead());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getHealthNow());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getHealthMax());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getEnergyNow());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getEnergyMax());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getHungerNow());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getHungerMax());
        this.response.appendInt(this.habbo.getHabboRoleplayStats().getCorporation().getId());
        this.response.appendInt(0);
        this.response.appendBoolean(CorporationsShiftManager.getInstance().isUserWorking(this.habbo));
        if (this.habbo.getHabboRoleplayStats().getGang() != null) {
            this.response.appendInt(this.habbo.getHabboRoleplayStats().getGang().getId());
        } else {
            this.response.appendInt(0);
        }
        this.response.appendInt(0);
        return this.response;
    }
}
