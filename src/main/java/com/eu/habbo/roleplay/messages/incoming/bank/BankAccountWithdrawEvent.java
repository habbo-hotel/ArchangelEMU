package com.eu.habbo.roleplay.messages.incoming.bank;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.bank.BankAccountWithdrawCommand;

public class BankAccountWithdrawEvent extends MessageHandler {
    @Override
    public void handle() {
        String corpID = String.valueOf(this.packet.readInt());
        String withdrawAmount = this.packet.readString();

        if (corpID == null || withdrawAmount == null) {
            return;
        }

        new BankAccountWithdrawCommand().handle(this.client, new String[] {null, corpID, withdrawAmount});
    }
}