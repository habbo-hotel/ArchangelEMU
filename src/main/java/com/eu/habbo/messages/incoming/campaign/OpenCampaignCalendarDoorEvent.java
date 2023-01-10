package com.eu.habbo.messages.incoming.campaign;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

public class OpenCampaignCalendarDoorEvent extends MessageHandler {
    @Override
    public void handle() {
        String campaignName = this.packet.readString();
        int day = this.packet.readInt();

        Emulator.getGameEnvironment().getCalendarManager().claimCalendarReward(this.client.getHabbo(), campaignName, day, false);
    }
}
