package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guides.GuideOnDutyStatusMessageComposer;

public class GuideSessionOnDutyUpdateEvent extends MessageHandler {
    @Override
    public void handle() {
        boolean onDuty = this.packet.readBoolean();

        if (onDuty) {
            boolean tourRequests = this.packet.readBoolean();
            boolean helperRequests = this.packet.readBoolean();
            boolean bullyReports = this.packet.readBoolean();

            if (!this.client.getHabbo().hasRight(Permission.ACC_HELPER_USE_GUIDE_TOOL))
                return;

            if (helperRequests && !this.client.getHabbo().hasRight(Permission.ACC_HELPER_GIVE_GUIDE_TOURS))
                helperRequests = false;

            if (bullyReports && !this.client.getHabbo().hasRight(Permission.ACC_HELPER_JUDGE_CHAT_REVIEWS))
                bullyReports = false;

            if (helperRequests) {
                Emulator.getGameEnvironment().getGuideManager().setOnGuide(this.client.getHabbo(), onDuty);
            }

            if (bullyReports) {
                Emulator.getGameEnvironment().getGuideManager().setOnGuardian(this.client.getHabbo(), onDuty);
            }

            this.client.sendResponse(new GuideOnDutyStatusMessageComposer(onDuty));
        } else {
            Emulator.getGameEnvironment().getGuideManager().setOnGuide(this.client.getHabbo(), onDuty);
            Emulator.getGameEnvironment().getGuideManager().setOnGuardian(this.client.getHabbo(), onDuty);
            this.client.sendResponse(new GuideOnDutyStatusMessageComposer(onDuty));
        }
    }
}