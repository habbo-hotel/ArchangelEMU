package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamecenter.GameCenterAchievementsConfigurationComposer;
import com.eu.habbo.messages.outgoing.gamecenter.basejump.JoinedQueueMessageComposer;
import com.eu.habbo.messages.outgoing.gamecenter.basejump.LoadGameMessageComposer;
import com.eu.habbo.messages.outgoing.gamecenter.basejump.LoadGameUrlMessageComposer;

public class JoinQueueEvent extends MessageHandler {
    @Override
    public void handle() {
        int gameId = this.packet.readInt();

        if (gameId == 3) //BaseJump
        {
            this.client.sendResponse(new GameCenterAchievementsConfigurationComposer());
            this.client.sendResponse(new LoadGameUrlMessageComposer());
            this.client.sendResponse(new LoadGameMessageComposer(this.client, 3));
        } else if (gameId == 4) {
            this.client.sendResponse(new JoinedQueueMessageComposer(4));
            this.client.sendResponse(new LoadGameUrlMessageComposer());
        }
    }
}