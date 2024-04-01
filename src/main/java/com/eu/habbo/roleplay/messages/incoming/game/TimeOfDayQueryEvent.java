package com.eu.habbo.roleplay.messages.incoming.game;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.game.TimeOfDayComposer;

public class TimeOfDayQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new TimeOfDayComposer());

    }
}