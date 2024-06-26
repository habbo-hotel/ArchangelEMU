package com.eu.habbo.roleplay.messages.incoming.bank;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.bank.BankAccountDepositCommand;

public class BankAccountDepositEvent extends MessageHandler {
    @Override
    public void handle() {
        String corpID = String.valueOf(this.packet.readInt());
        String depositAmount = String.valueOf(this.packet.readInt());

        if (corpID == null || depositAmount == null) {
            return;
        }

        new BankAccountDepositCommand().handle(this.client, new String[] {null, corpID, depositAmount});
    }
}