package com.eu.habbo.roleplay.billing;

import com.eu.habbo.Emulator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

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

    private BillingManager() {
        long millis = System.currentTimeMillis();
        LOGGER.info("Billing Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public UserBill getBillByID(int billingStatementID) {
        String query = "SELECT * FROM rp_users_bills WHERE id = ?";
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, billingStatementID);

            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    return new UserBill(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return null;
    }
}
