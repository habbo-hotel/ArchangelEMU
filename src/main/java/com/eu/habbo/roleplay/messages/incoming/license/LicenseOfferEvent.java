package com.eu.habbo.roleplay.messages.incoming.license;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.license.LicenseConnectComputerCommand;
import com.eu.habbo.roleplay.commands.license.LicenseOfferCommand;
import com.eu.habbo.roleplay.government.LicenseType;

public class LicenseOfferEvent extends MessageHandler {
    @Override
    public void handle() {
        String username = this.packet.readString();
        LicenseType licenseType = LicenseType.fromValue(this.packet.readInt());

        if (username == null) {
            return;
        }

        new LicenseOfferCommand().handle(this.client, new String[] {null, username, String.valueOf(licenseType.getValue())});
    }
}