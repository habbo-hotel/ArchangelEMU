package com.eu.habbo.roleplay.messages.incoming.bank;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.bank.BankAccountTransferDebitCommand;

public class BankAccountTransferDebitEvent extends MessageHandler {
    @Override
    public void handle() {
        String corpID = String.valueOf(this.packet.readInt());
        String transferAmount = this.packet.readString();

        if (corpID == null || transferAmount == null) {
            return;
        }

        new BankAccountTransferDebitCommand().handle(this.client, new String[] {null, corpID, transferAmount});
    }
}