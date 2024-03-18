package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamecenter.Game2AccountGameStatusMessageComposer;

public class GetGameStatusEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new Game2AccountGameStatusMessageComposer(this.packet.readInt(), 10));
    }
}