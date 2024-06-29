package com.eu.habbo.roleplay.messages.incoming.license;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.license.LicenseStatusCommand;

public class LicenseStatusQueryEvent extends MessageHandler {
    @Override
    public void handle() {
        String corpID = String.valueOf(this.packet.readInt());
        String username = this.packet.readString();

        if (corpID == null || username == null) {
            return;
        }

        new LicenseStatusCommand().handle(this.client, new String[] {null, corpID, username});
    }
}