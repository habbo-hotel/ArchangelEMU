package com.eu.habbo.roleplay.billing.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.government.GovernmentManager;
import com.eu.habbo.roleplay.government.LicenseType;

import java.util.List;

public class MiningLicenseBillingItem implements BillingItem{

    private  int userID;
    private int chargedByUserID;

    public MiningLicenseBillingItem(int userID, int chargedByUserID) {
        this.userID = userID;
        this.chargedByUserID = chargedByUserID;
    }

    @Override
    public BillType getType() {
        return BillType.MINING_LICENSE;
    }

    @Override
    public String getTitle() {
        return "Mining License";
    }

    @Override
    public String getDescription() {
        return "Fee for processing";
    }

    @Override
    public int getAmountOwed() {
        return 150;
    }

    @Override
    public int getAmountPaid() {
        return 0;
    }

    @Override
    public int getChargedByCorpID() {
        return GovernmentManager.getInstance().getMiningCorp().getGuild().getId();
    }

    @Override
    public int chargedByUserID() {
        return this.chargedByUserID;
    }

    @Override
    public int userID() {
        return this.userID;
    }

    @Override
    public void onBillPaid(Habbo habbo) {
        habbo.getInventory().getLicensesComponent().createLicense(LicenseType.MINING);
        habbo.shout(Emulator.getTexts().getValue("roleplay.license.received").replace(":license", this.getTitle()));
    }
}
