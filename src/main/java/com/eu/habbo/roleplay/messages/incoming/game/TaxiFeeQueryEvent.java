package com.eu.habbo.roleplay.messages.incoming.game;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.game.TaxiFeeComposer;

public class TaxiFeeQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new TaxiFeeComposer());

    }
}