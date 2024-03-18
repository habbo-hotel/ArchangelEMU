package com.eu.habbo.threading.runnables.freeze;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FreezeClearEffects implements Runnable {
    private final Habbo habbo;


    @Override
    public void run() {
        this.habbo.getRoomUnit().setEffectId(0);
        this.habbo.getRoomUnit().setEffectEndTimestamp(0);
        this.habbo.getRoomUnit().setCanWalk(true);
        if (this.habbo.getRoomUnit().getRoom() != null) {
            this.habbo.getRoomUnit().getRoom().sendComposer(new AvatarEffectMessageComposer(this.habbo.getRoomUnit()).compose());
        }
    }
}
