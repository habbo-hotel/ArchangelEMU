package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TObjectProcedure;

public class UserEffectsListComposer extends MessageComposer {
    public final Habbo habbo;

    public UserEffectsListComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.UserEffectsListComposer);

        if (this.habbo == null || this.habbo.getInventory() == null || this.habbo.getInventory().getEffectsComponent() == null || this.habbo.getInventory().getEffectsComponent().effects == null) {
            this.response.appendInt(0);
        } else {
            synchronized (this.habbo.getInventory().getEffectsComponent().effects) {
                this.response.appendInt(this.habbo.getInventory().getEffectsComponent().effects.size());
                this.habbo.getInventory().getEffectsComponent().effects.forEachValue(effect -> {
                    UserEffectsListComposer.this.response.appendInt(effect.effect);
                    UserEffectsListComposer.this.response.appendInt(0);
                    UserEffectsListComposer.this.response.appendInt(effect.duration > 0 ? effect.duration : 1);
                    UserEffectsListComposer.this.response.appendInt(effect.total - (effect.isActivated() ? 1 : 0));

                    if(!effect.isActivated()) {
                        UserEffectsListComposer.this.response.appendInt(0);
                    }
                    else {
                        UserEffectsListComposer.this.response.appendInt(effect.duration > 0 ? (Emulator.getIntUnixTimestamp() - effect.activationTimestamp) + effect.duration : -1);
                    }
                    UserEffectsListComposer.this.response.appendBoolean(effect.duration <= 0); //effect.isActivated());
                    return true;
                });
            }
        }

        return this.response;
    }
}
