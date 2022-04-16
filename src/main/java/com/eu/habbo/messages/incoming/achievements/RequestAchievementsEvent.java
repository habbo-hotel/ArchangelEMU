package com.eu.habbo.messages.incoming.achievements;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.achievements.AchievementsComposer;

public class RequestAchievementsEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new AchievementsComposer(this.client.getHabbo()));
    }
}
