package com.eu.habbo.roleplay.messages.outgoing.bank;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BankOpenComputerComposer extends MessageComposer {
    private final int itemID;
    private final int corpID;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.bankOpenComputerComposer);
        this.response.appendInt(this.itemID);
        this.response.appendInt(this.corpID);
        return this.response;
    }
}
