package com.eu.habbo.habbohotel.users.subscriptions;

import com.eu.habbo.Emulator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Beny
 */
@Getter
@Slf4j
@AllArgsConstructor
public class Subscription {
    public static final String HABBO_CLUB = "HABBO_CLUB";

    private final int id;
    private final int userId;
    private final String subscriptionType;
    private final int timestampStart;
    private int duration;
    private boolean active;

    public void addDuration(int amount) {
        this.duration += amount;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users_subscriptions` SET `duration` = ? WHERE `id` = ? LIMIT 1")) {
                statement.setInt(1, this.duration);
                statement.setInt(2, this.id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    /**
     * Sets the subscription as active or inactive. If active and remaining time <= 0 the SubscriptionScheduler will inactivate the subscription and call onExpired()
     * @param active Boolean indicating if the subscription is active
     */
    public void setActive(boolean active) {
        this.active = active;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users_subscriptions` SET `active` = ? WHERE `id` = ? LIMIT 1")) {
                statement.setInt(1, this.active ? 1 : 0);
                statement.setInt(2, this.id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    /**
     * @return Remaining duration of subscription in seconds
     */
    public int getRemaining() {
        return (this.timestampStart + this.duration) - Emulator.getIntUnixTimestamp();
    }

    /**
     * @return Unix timestamp end of subscription
     */
    public int getTimestampEnd() {
        return (this.timestampStart + this.duration);
    }


    /**
     * Called when the subscription is first created
     */
    public void onCreated() { }

    /**
     * Called when the subscription is extended or bought again when already exists
     * @param duration Extended duration time in seconds
     */
    public void onExtended(int duration) { }

    /**
     * Called by SubscriptionScheduler when isActive() && getRemaining() < 0
     */
    public void onExpired() { }
}
