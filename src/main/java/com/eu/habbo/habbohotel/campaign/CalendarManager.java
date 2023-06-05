package com.eu.habbo.habbohotel.campaign;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.events.calendar.CampaignCalendarDoorOpenedMessageComposer;
import com.eu.habbo.plugin.events.users.calendar.UserClaimRewardEvent;
import gnu.trove.map.hash.THashMap;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.*;



@Slf4j
public class CalendarManager {

    private static final Map<Integer, CalendarCampaign> calendarCampaigns = new THashMap<>();
    public static double HC_MODIFIER;

    public CalendarManager() {
        long millis = System.currentTimeMillis();
        this.reload();
        log.info("Calendar Manager -> Loaded! ({} MS)", (System.currentTimeMillis() - millis));
    }

    public void dispose() {
        calendarCampaigns.clear();
    }

    public boolean reload() {
        this.dispose();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM calendar_campaigns WHERE enabled = 1")) {
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    calendarCampaigns.put(set.getInt("id"), new CalendarCampaign(set));
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
            return false;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM calendar_rewards")) {
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    CalendarCampaign campaign = calendarCampaigns.get(set.getInt("campaign_id"));
                    if (campaign != null) {
                        campaign.addReward(new CalendarRewardObject(set));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
            return false;
        }

        CalendarManager.HC_MODIFIER = Emulator.getConfig().getDouble("hotel.calendar.pixels.hc_modifier", 2.0);

        return true;
    }

    public void addCampaign(CalendarCampaign campaign) {

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO calendar_campaigns ( name, image, start_timestamp, total_days, lock_expired) VALUES (?, ?, ?, ? , ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, campaign.getName());
            statement.setString(2, campaign.getImage());
            statement.setInt(3, campaign.getStartTimestamp());
            statement.setInt(4, campaign.getTotalDays());
            statement.setBoolean(5, campaign.getLockExpired());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating calendar campaign failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    campaign.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating calendar campaign failed, no ID found.");
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        calendarCampaigns.put(campaign.getId(), campaign);
    }

    public boolean deleteCampaign(CalendarCampaign campaign) {
        calendarCampaigns.remove(campaign.getId());

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM calendar_campaigns WHERE id = ? LIMIT 1")) {
            statement.setInt(1, campaign.getId());
            return statement.execute();
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        return false;
    }

    public CalendarCampaign getCalendarCampaign(String campaignName) {
        return calendarCampaigns.values().stream().filter(cc -> Objects.equals(cc.getName(), campaignName)).findFirst().orElse(null);
    }

    public Map<Integer, CalendarCampaign> getCalendarCampaigns() {
        return calendarCampaigns;
    }

    public void claimCalendarReward(Habbo habbo, String campaignName, int day, boolean force) {
        CalendarCampaign campaign = calendarCampaigns.values().stream().filter(cc -> Objects.equals(cc.getName(), campaignName)).findFirst().orElse(null);
        if (campaign == null || campaign.getRewards().isEmpty() || (habbo.getHabboStats().getCalendarRewardsClaimed().stream().anyMatch(claimed -> claimed.getCampaignId() == campaign.getId() && claimed.getDay() == day)))
            return;

        List<CalendarRewardObject> rewards = new ArrayList<>(campaign.getRewards().values());
        CalendarRewardObject object = rewards.get(Emulator.getRandom().nextInt(rewards.size() - 1 + 1));

        if (object == null) return;
        long daysBetween = ChronoUnit.DAYS.between(new Timestamp(campaign.getStartTimestamp() * 1000L).toInstant(), new Date().toInstant());
        if (((daysBetween >= 0 && daysBetween <= campaign.getTotalDays()) && (((daysBetween - day <= 2 || !campaign.getLockExpired()) && daysBetween - day >= 0)) || (force && habbo.hasRight(Permission.ACC_CALENDAR_FORCE)))) {
            if (Emulator.getPluginManager().fireEvent(new UserClaimRewardEvent(habbo, campaign, day, object, force)).isCancelled()) {
                return;
            }
            habbo.getHabboStats().getCalendarRewardsClaimed().add(new CalendarRewardClaimed(habbo.getHabboInfo().getId(), campaign.getId(), day, object.getId(), new Timestamp(System.currentTimeMillis())));
            habbo.getClient().sendResponse(new CampaignCalendarDoorOpenedMessageComposer(true, object, habbo));
            object.give(habbo);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO calendar_rewards_claimed (user_id, campaign_id, day, reward_id, timestamp) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, habbo.getHabboInfo().getId());
                statement.setInt(2, campaign.getId());
                statement.setInt(3, day);
                statement.setInt(4, object.getId());
                statement.setInt(5, Emulator.getIntUnixTimestamp());
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
    }
}

