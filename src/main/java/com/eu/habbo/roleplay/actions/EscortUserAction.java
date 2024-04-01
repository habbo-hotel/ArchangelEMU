package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EscortUserAction implements Runnable {
    private final Habbo user;
    private final Habbo userBeingEscorted;
    private final int directionOffset;

    @Override
    public void run() {
        if (this.user != null && this.user.getRoomUnit() != null && this.userBeingEscorted != null && this.userBeingEscorted.getRoomUnit() != null) {

            if (this.user.getHabboRoleplayStats().getIsEscorting() == null || this.userBeingEscorted.getHabboRoleplayStats().getEscortedBy() == null) {
                this.user.getHabboRoleplayStats().setIsEscorting(null);
                return;
            }

            RoomTile target = this.user.getRoomUnit().getRoom().getLayout().getTileInFront(this.user.getRoomUnit().getCurrentPosition(), Math.abs((this.user.getRoomUnit().getBodyRotation().getValue() + this.directionOffset + 4) % 8));

            if (target != null && target.getX() >= 0 && target.getY() >= 0) {
                this.userBeingEscorted.getRoomUnit().walkTo(target);
                this.userBeingEscorted.getRoomUnit().setCanWalk(true);
            }

            Emulator.getThreading().run(this, 20);
        }
    }
}
