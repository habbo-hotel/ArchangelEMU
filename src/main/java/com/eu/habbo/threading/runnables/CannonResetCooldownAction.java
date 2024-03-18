package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.items.interactions.InteractionCannon;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CannonResetCooldownAction implements Runnable {
    private final InteractionCannon cannon;

    @Override
    public void run() {
        if (this.cannon != null) {
            this.cannon.cooldown = false;
        }
    }
}
