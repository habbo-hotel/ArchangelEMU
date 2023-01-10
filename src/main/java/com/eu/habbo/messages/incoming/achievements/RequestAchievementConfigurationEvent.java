package com.eu.habbo.messages.incoming.achievements;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.BadgePointLimitsComposer;

public class RequestAchievementConfigurationEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BadgePointLimitsComposer());
    }
}
