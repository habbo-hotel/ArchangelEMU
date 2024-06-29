package com.eu.habbo.roleplay.messages.incoming.license;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.license.LicenseConnectComputerCommand;

public class LicenseConnectComputerEvent extends MessageHandler {
    @Override
    public void handle() {
        String itemID = String.valueOf(this.packet.readInt());
        String corpID = String.valueOf(this.packet.readInt());

        if (itemID == null || corpID == null) {
            return;
        }

        new LicenseConnectComputerCommand().handle(this.client, new String[] {null, itemID, corpID});
    }
}