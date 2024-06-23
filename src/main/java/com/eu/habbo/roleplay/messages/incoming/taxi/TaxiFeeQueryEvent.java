package com.eu.habbo.roleplay.messages.incoming.taxi;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.taxi.TaxiFeeComposer;

public class TaxiFeeQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new TaxiFeeComposer());

    }
}