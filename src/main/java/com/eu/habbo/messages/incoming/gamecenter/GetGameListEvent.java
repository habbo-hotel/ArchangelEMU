package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamecenter.GameCenterAchievementsConfigurationComposer;

public class GetGameListEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new GameCenterAchievementsConfigurationComposer());
    }
}