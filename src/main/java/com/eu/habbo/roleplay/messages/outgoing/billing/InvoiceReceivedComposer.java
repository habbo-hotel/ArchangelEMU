package com.eu.habbo.roleplay.messages.outgoing.billing;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.billing.UserBill;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvoiceReceivedComposer extends MessageComposer {
    private final UserBill userBill;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.invoiceReceivedComposer);
        this.response.appendInt(this.userBill.id);
        this.response.appendInt(this.userBill.userID);
        this.response.appendString(this.userBill.type.getValue());
        this.response.appendString(this.userBill.title);
        this.response.appendString(this.userBill.description);
        this.response.appendInt(this.userBill.chargedByUserID);
        this.response.appendInt(this.userBill.chargedByCorpID);
        this.response.appendInt(this.userBill.amountCharged);
        this.response.appendInt(this.userBill.amountPaid);
        return this.response;
    }
}
