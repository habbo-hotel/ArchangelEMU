package com.eu.habbo.roleplay.billing;

import com.eu.habbo.roleplay.billing.items.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eu.habbo.roleplay.database.HabboBillRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserBill {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserBill.class);

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
        if (this.type == BillType.DRIVER_LICENSE) {
            return new DriverLicenseBillingItem(this.userID, this.chargedByUserID);
        }
        if (this.type == BillType.FARMING_LICENSE) {
            return new FarmingLicenseBillingItem(this.userID, this.chargedByUserID);
        }
        if (this.type == BillType.FISHING_LICENSE) {
            return new FishingLicenseBillingItem(this.userID, this.chargedByUserID);
        }

        if (this.type == BillType.MINING_LICENSE) {
            return new MiningLicenseBillingItem(this.userID, this.chargedByUserID);
        }

        if (this.type == BillType.WEAPON_LICENSE) {
            return new WeaponLicenseBillingItem(this.userID, this.chargedByUserID);
        }

        throw new Error("invalid bill type");
    }

    public void save() {
        HabboBillRepository.getInstance().update(this);
    }

    public void delete() {
        HabboBillRepository.getInstance().delete(this.id);
    }
}
