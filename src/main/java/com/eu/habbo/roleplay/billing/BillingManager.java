package com.eu.habbo.roleplay.government;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.billing.BillingStatement;
import com.eu.habbo.roleplay.weapons.Weapon;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class BillingManager {

    private static BillingManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingManager.class);

    public static BillingManager getInstance() {
        if (instance == null) {
            instance = new BillingManager();
        }
        return instance;
    }

    private TIntObjectHashMap<BillingStatement> bills;

    private BillingManager() {
        long millis = System.currentTimeMillis();
        this.bills = new TIntObjectHashMap<>();
        this.loadBills();
        LOGGER.info("Billing Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public BillingStatement getBillByID(int billingStatementID) {
        return this.bills.get(billingStatementID);
    }

    public void reload() {
        this.loadBills();
    }

    private void loadBills() {
        this.bills.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_users_bills ORDER BY id ASC")) {
            while (set.next()) {
                BillingStatement bill = null;
                if (!this.bills.containsKey(set.getInt("id"))) {
                    bill = new BillingStatement(set);
                } else {
                    bill = BillingStatement.loadById(set.getInt("id"));
                }
                this.bills.put(set.getInt("id"), bill);
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.bills = null;
    }
}
