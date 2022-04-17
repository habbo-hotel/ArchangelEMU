package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamecenter.basejump.UnloadGameMessageComposer;

public class GameCenterLeaveGameEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new UnloadGameMessageComposer());
    }
}