package com.eu.habbo.roleplay.messages.incoming.bank;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.bank.BankAccountCloseCommand;

public class BankAccountCloseEvent extends MessageHandler {
    @Override
    public void handle() {
        String username = this.packet.readString();

        if (username == null) {
            return;
        }

        new BankAccountCloseCommand().handle(this.client, new String[] {null, username});
    }
}
