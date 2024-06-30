package com.eu.habbo.roleplay.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.interactions.InteractionHospitalBed;
import com.eu.habbo.roleplay.room.RoomType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HospitalRecoveryAction implements Runnable {

    public static int HEALTH_PER_CYCLE = 5;

    private final Habbo habbo;

    @Override
    public void run() {

        if (this.habbo.getRoomUnit().getRoom() == null) {
            return;
        }

        if (!this.habbo.getRoomUnit().getRoom().getRoomInfo().getTags().contains(RoomType.HOSPITAL)) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.hospital.cancel_recovery"));
            return;
        }

        if (this.habbo.getRoomUnit().getCurrentItem().getBaseItem().getInteractionType().getType() == InteractionHospitalBed.class) {
            this.habbo.shout(Emulator.getTexts().getValue("roleplay.hospital.cancel_recovery"));
            return;
        }

        if (this.habbo.getHabboRoleplayStats().getHealthNow() >= this.habbo.getHabboRoleplayStats().getHealthMax()) {
            this.onHealingComplete();
            return;
        }

        this.habbo.getHabboRoleplayStats().setHealth(this.habbo.getHabboRoleplayStats().getHealthNow() + HospitalRecoveryAction.HEALTH_PER_CYCLE);

        this.habbo.shout(Emulator.getTexts()
                .getValue("roleplay.hospital.progress_recovery")
                .replace(":healthNow", String.valueOf(this.habbo.getHabboRoleplayStats().getHealthNow()))
                .replace(":healthMax", String.valueOf(this.habbo.getHabboRoleplayStats().getHealthMax()))
        );
        Emulator.getThreading().run(this, 2500);
    }

    public void onHealingComplete() {

        this.habbo.shout(Emulator.getTexts().getValue("roleplay.hospital.finish_recovery"));
    }

}
