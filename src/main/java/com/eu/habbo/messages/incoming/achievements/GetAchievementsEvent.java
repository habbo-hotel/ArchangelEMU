package com.eu.habbo.messages.incoming.achievements;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.achievements.AchievementsComposer;

public class GetAchievementsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new AchievementsComposer(this.client.getHabbo()));
    }
}
