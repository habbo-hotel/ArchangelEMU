package com.eu.habbo.roleplay.billing.items;

import com.eu.habbo.habbohotel.users.Habbo;

public interface BillingItem {
    public BillType getType();
    public String getTitle();
    public String getDescription();
    public int userID();
    public int chargedByUserID();
    public int getChargedByCorpID();
    public int getAmountOwed();
    public int getAmountPaid();
    public void onBillPaid(Habbo habbo);
}
