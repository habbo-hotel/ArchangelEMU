package com.eu.habbo.roleplay.messages.incoming.bank;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.messages.outgoing.bank.BankListComposer;

public class BankListEvent extends MessageHandler {
    @Override
    public void handle() {
        this.client.sendResponse(new BankListComposer());
    }
}