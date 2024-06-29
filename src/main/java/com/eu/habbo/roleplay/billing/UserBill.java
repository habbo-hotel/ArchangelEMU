package com.eu.habbo.roleplay.billing;

import com.eu.habbo.roleplay.billing.items.*;
import com.eu.habbo.roleplay.database.HabboBillRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBill {

    public int id;
    public int userID;
    public int chargedByUserID;
    public int chargedByCorpID;
    public BillType type;
    public String title;
    public String description;
    public int amountCharged;
    public int amountPaid;

    public UserBill(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.userID = set.getInt("user_id");
        this.chargedByUserID = set.getInt("charged_by_user_id");
        this.chargedByCorpID = set.getInt("charged_by_corp_id");
        this.type = BillType.fromValue(set.getString("type"));
        this.title = set.getString("title");
        this.description = set.getString("description");
        this.amountCharged = set.getInt("amount_charged");
        this.amountPaid = set.getInt("amount_paid");
    }

    public BillingItem getBillingItem() {
        switch (this.type) {
            case DRIVER_LICENSE:
                return new DriverLicenseBillingItem(this.userID, this.chargedByUserID);
            case FARMING_LICENSE:
                return new FarmingLicenseBillingItem(this.userID, this.chargedByUserID);
            case FISHING_LICENSE:
                return new FishingLicenseBillingItem(this.userID, this.chargedByUserID);
            case MINING_LICENSE:
                return new MiningLicenseBillingItem(this.userID, this.chargedByUserID);
            case LUMBERJACK_LICENSE:
                return new LumberjackLicenseBillingItem(this.userID, this.chargedByUserID);
            case WEAPON_LICENSE:
                return new WeaponLicenseBillingItem(this.userID, this.chargedByUserID);
            default:
                throw new IllegalArgumentException("Invalid bill type: " + this.type);
        }
    }

    public void save() {
        HabboBillRepository.getInstance().update(this);
    }

    public void delete() {
        HabboBillRepository.getInstance().delete(this.id);
    }
}
