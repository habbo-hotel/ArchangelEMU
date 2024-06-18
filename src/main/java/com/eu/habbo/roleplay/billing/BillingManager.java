package com.eu.habbo.roleplay.billing;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.billing.items.BillingItem;
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

    private TIntObjectHashMap<BillingStatement> billingStatements;

    private BillingManager() {
        long millis = System.currentTimeMillis();
        LOGGER.info("Billing Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public BillingStatement createBillingStatement(BillingItem billingItem) {
        return this.createBillingStatement(billingItem.getUserID(), billingItem.getChargedByUserID(), billingItem.getChargedByCorpID(), billingItem.getTitle(), billingItem.getDescription(), billingItem.getAmountOwed());
    }

    public BillingStatement createBillingStatement(int userID, int chargedByUserID, int chargedByCorpID, String title, String description, int amountCharged) {
        BillingStatement billingStatement= BillingStatement.create(userID, chargedByUserID, chargedByCorpID, title, description, amountCharged);
        return this.billingStatements.put(billingStatement.id, billingStatement);
    }

    public BillingStatement getBillingStatementByID(int billingStatementID) {
        return this.billingStatements.get(billingStatementID);
    }

    public void reload() {
        this.loadBillingStatements();
    }

    private void loadBillingStatements() {
        this.billingStatements.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM rp_users_bills ORDER BY id ASC")) {
            while (set.next()) {
                BillingStatement billingStatement = null;
                if (!this.billingStatements.containsKey(set.getInt("id"))) {
                    billingStatement = new BillingStatement(set);
                    this.billingStatements.put(set.getInt("id"), billingStatement);
                } else {
                    billingStatement = this.billingStatements.get(set.getInt("id"));
                    billingStatement.load(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void dispose() {
        this.billingStatements = null;
    }
}
