package com.eu.habbo.roleplay.messages.incoming.billing;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.roleplay.commands.billing.PayBillCommand;

public class PayBillEvent extends MessageHandler {
    @Override
    public void handle() {
        String billID = this.packet.readString();

        if (billID == null) {
            return;
        }

        new PayBillCommand().handle(this.client, new String[] {null, billID});
    }
}