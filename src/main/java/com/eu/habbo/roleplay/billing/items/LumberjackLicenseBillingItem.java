package com.eu.habbo.roleplay.billing.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.government.GovernmentManager;
import com.eu.habbo.roleplay.government.LicenseType;

public record LumberjackLicenseBillingItem(int userID, int chargedByUserID) implements BillingItem {

    @Override
    public BillType getType() {
        return BillType.LUMBERJACK_LICENSE;
    }

    @Override
    public String getTitle() {
        return "Lumberjack License";
    }

    @Override
    public String getDescription() {
        return "Fee for forest restoration and preservation";
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
    public void onBillPaid(Habbo habbo) {
        habbo.getInventory().getLicensesComponent().createLicense(LicenseType.LUMBERJACK);
        habbo.shout(Emulator.getTexts().getValue("roleplay.license.received").replace(":license", this.getTitle()));
    }
}
