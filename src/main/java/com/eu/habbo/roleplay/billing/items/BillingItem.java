package com.eu.habbo.roleplay.billing.items;

public interface BillingItem {
    public String getTitle();
    public String getDescription();
    public int getUserID();
    public int getChargedByUserID();
    public int getChargedByCorpID();
    public int getAmountOwed();
    public int getAmountPaid();
}
