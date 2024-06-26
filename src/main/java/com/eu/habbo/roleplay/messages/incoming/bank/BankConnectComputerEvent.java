package com.eu.habbo.roleplay.messages.incoming.bank;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.bank.BankConnectATMCommand;

public class BankConnectComputerEvent extends MessageHandler {
    @Override
    public void handle() {
        String itemID = String.valueOf(this.packet.readInt());
        String corpID = String.valueOf(this.packet.readInt());

        if (itemID == null || corpID == null) {
            return;
        }

        new BankConnectATMCommand().handle(this.client, new String[] {null, itemID, corpID});
    }
}