package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamecenter.basejump.UnloadGameMessageComposer;

public class GameUnloadedEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new UnloadGameMessageComposer());
    }
}