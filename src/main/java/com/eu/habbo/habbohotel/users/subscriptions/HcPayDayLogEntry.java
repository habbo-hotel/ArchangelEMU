package com.eu.habbo.habbohotel.users.subscriptions;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.DatabaseLoggable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Beny
 */
@Getter
@AllArgsConstructor
public class HcPayDayLogEntry implements Runnable, DatabaseLoggable {
    private static final String QUERY = "INSERT INTO `logs_hc_payday` (`timestamp`, `user_id`, `hc_streak`, `total_coins_spent`, `reward_coins_spent`, `reward_streak`, `total_payout`, `currency`, `claimed`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final int timestamp;
    private final int userId;
    private final int hcStreak;
    private final int totalCoinsSpent;
    private final int rewardCoinsSpent;
    private final int rewardStreak;
    private final int totalPayout;
    private final String currency;
    private final boolean claimed;


    @Override
    public String getQuery() {
        return QUERY;
    }

    @Override
    public void log(PreparedStatement statement) throws SQLException {
        statement.setInt(1, this.timestamp);
        statement.setInt(2, this.userId);
        statement.setInt(3, this.hcStreak);
        statement.setInt(4, this.totalCoinsSpent);
        statement.setInt(5, this.rewardCoinsSpent);
        statement.setInt(6, this.rewardStreak);
        statement.setInt(7, this.totalPayout);
        statement.setString(8, this.currency);
        statement.setInt(9, this.claimed ? 1 : 0);
        statement.addBatch();
    }

    @Override
    public void run() {
        Emulator.getDatabaseLogger().store(this);
    }
}
