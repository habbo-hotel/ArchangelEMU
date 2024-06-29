package com.eu.habbo.roleplay.messages.incoming.license;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.license.LicenseAgencyListComposer;

public class LicenseAgencyListEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new LicenseAgencyListComposer());
    }
}