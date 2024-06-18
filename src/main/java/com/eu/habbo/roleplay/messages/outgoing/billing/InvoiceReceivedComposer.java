package com.eu.habbo.roleplay.messages.outgoing.billing;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.billing.BillingStatement;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvoiceReceivedComposer extends MessageComposer {
    private final BillingStatement billingStatement;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.invoiceReceivedComposer);
        this.response.appendInt(this.billingStatement.id);
        this.response.appendInt(this.billingStatement.userID);
        this.response.appendString(this.billingStatement.title);
        this.response.appendString(this.billingStatement.description);
        this.response.appendInt(this.billingStatement.chargedByUserID);
        this.response.appendInt(this.billingStatement.chargedByCorpID);
        this.response.appendInt(this.billingStatement.amountCharged);
        this.response.appendInt(this.billingStatement.amountPaid);
        this.response.appendInt(this.billingStatement.createdAt);
        this.response.appendInt(this.billingStatement.updatedAt);
        return this.response;
    }
}
