package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorPreferencesComposer;

public class GetUserEventCatsEvent extends MessageHandler {
    @Override
    public void handle() {


        this.client.sendResponse(new NewNavigatorPreferencesComposer(this.client.getHabbo().getHabboStats().getNavigatorWindowSettings()));

    }
}
