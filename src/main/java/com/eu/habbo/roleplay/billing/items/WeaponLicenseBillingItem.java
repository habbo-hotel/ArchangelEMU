package com.eu.habbo.roleplay.billing.items;

import com.eu.habbo.roleplay.government.GovernmentManager;

public class WeaponLicenseBillingItem implements BillingItem{

    private  int userID;
    private int chargedByUserID;

    public WeaponLicenseBillingItem(int userID, int chargedByUserID) {
        this.userID = userID;
        this.chargedByUserID = chargedByUserID;
    }

    @Override
    public String getTitle() {
        return "Weapon License";
    }

    @Override
    public String getDescription() {
        return "Fee for background check and processing";
    }

    @Override
    public int getAmountOwed() {
        return 500;
    }

    @Override
    public int getAmountPaid() {
        return 0;
    }

    @Override
    public int getChargedByCorpID() {
        return GovernmentManager.getInstance().getPoliceCorp().getGuild().getId();
    }

    @Override
    public int getChargedByUserID() {
        return this.chargedByUserID;
    }

    @Override
    public int getUserID() {
        return this.userID;
    }
}
