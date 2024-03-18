package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.AccountPreferencesComposer;

public class GetSoundSettingsEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new AccountPreferencesComposer(this.client.getHabbo()));
    }
}
