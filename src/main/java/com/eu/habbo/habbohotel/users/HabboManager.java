package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.permissions.PermissionGroup;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceConfigurationComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.HabboBroadcastMessageComposer;
import com.eu.habbo.messages.outgoing.modtool.ModeratorInitMessageComposer;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;
import com.eu.habbo.messages.outgoing.users.UserRightsMessageComposer;
import com.eu.habbo.plugin.events.users.UserRankChangedEvent;
import com.eu.habbo.plugin.events.users.UserRegisteredEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HabboManager {

    //Configuration. Loaded from database & updated accordingly.
    public static String WELCOME_MESSAGE = "";
    public static boolean NAMECHANGE_ENABLED = false;

    @Getter
    private final ConcurrentHashMap<Integer, Habbo> onlineHabbos;

    public HabboManager() {
        long millis = System.currentTimeMillis();

        this.onlineHabbos = new ConcurrentHashMap<>();

        log.info("Habbo Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public static HabboInfo getOfflineHabboInfo(int id) {
        HabboInfo info = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ? LIMIT 1")) {
            statement.setInt(1, id);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    info = new HabboInfo(set);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        return info;
    }

    public static HabboInfo getOfflineHabboInfo(String username) {
        HabboInfo info = null;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1")) {
            statement.setString(1, username);

            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    info = new HabboInfo(set);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        return info;
    }

    public void addHabbo(Habbo habbo) {
        this.onlineHabbos.put(habbo.getHabboInfo().getId(), habbo);
    }

    public void removeHabbo(Habbo habbo) {
        this.onlineHabbos.remove(habbo.getHabboInfo().getId());
    }

    public Habbo getHabbo(int id) {
        return this.onlineHabbos.get(id);
    }

    public Habbo getHabbo(String username) {
        synchronized (this.onlineHabbos) {
            for (Map.Entry<Integer, Habbo> map : this.onlineHabbos.entrySet()) {
                if (map.getValue().getHabboInfo().getUsername().equalsIgnoreCase(username))
                    return map.getValue();
            }
        }

        return null;
    }

    public Habbo loadHabbo(String sso) {
        Habbo habbo;
        int userId = 0;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE auth_ticket = ? LIMIT 1")) {
            statement.setString(1, sso);
            try (ResultSet s = statement.executeQuery()) {
                if (s.next()) {
                    userId = s.getInt("id");
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        habbo = this.cloneCheck(userId);
        if (habbo != null) {
            habbo.alert(Emulator.getTexts().getValue("loggedin.elsewhere"));
            Emulator.getGameServer().getGameClientManager().disposeClient(habbo.getClient());
            habbo = null;
        }

        ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().checkForBan(userId);
        if (ban != null) {
            return null;
        }


        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE auth_ticket = ? LIMIT 1")) {
            statement.setString(1, sso);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    habbo = new Habbo(set);

                    if (habbo.getHabboInfo().firstVisit) {
                        Emulator.getPluginManager().fireEvent(new UserRegisteredEvent(habbo));
                    }

                    if (!Emulator.debugging) {
                        try (PreparedStatement stmt = connection.prepareStatement("UPDATE users SET auth_ticket = ? WHERE id = ? LIMIT 1")) {
                            stmt.setString(1, "");
                            stmt.setInt(2, habbo.getHabboInfo().getId());
                            stmt.execute();
                        } catch (SQLException e) {
                            log.error("Caught SQL exception", e);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        } catch (Exception ex) {
            log.error("Caught exception", ex);
        }

        return habbo;
    }

    public HabboInfo getHabboInfo(int id) {
        if (this.getHabbo(id) == null) {
            return getOfflineHabboInfo(id);
        }
        return this.getHabbo(id).getHabboInfo();
    }

    public int getOnlineCount() {
        return this.onlineHabbos.size();
    }

    public Habbo cloneCheck(int id) {
        return Emulator.getGameServer().getGameClientManager().getHabbo(id);
    }

    public void sendPacketToHabbosWithPermission(ServerMessage message, String perm) {
        synchronized (this.onlineHabbos) {
            for (Habbo habbo : this.onlineHabbos.values()) {
                if (habbo.hasRight(perm)) {
                    habbo.getClient().sendResponse(message);
                }
            }
        }
    }

    public synchronized void dispose() {


//


        log.info("Habbo Manager -> Disposed!");
    }

    public ArrayList<HabboInfo> getCloneAccounts(Habbo habbo, int limit) {
        ArrayList<HabboInfo> habboInfo = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE ip_register = ? OR ip_current = ? AND id != ? ORDER BY id DESC LIMIT ?")) {
            statement.setString(1, habbo.getHabboInfo().getIpRegister());
            statement.setString(2, habbo.getHabboInfo().getIpLogin());
            statement.setInt(3, habbo.getHabboInfo().getId());
            statement.setInt(4, limit);

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    habboInfo.add(new HabboInfo(set));
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        return habboInfo;
    }

    public List<Map.Entry<Integer, String>> getNameChanges(int userId, int limit) {
        List<Map.Entry<Integer, String>> nameChanges = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT timestamp, new_name FROM namechange_log WHERE user_id = ? ORDER by timestamp DESC LIMIT ?")) {
            statement.setInt(1, userId);
            statement.setInt(2, limit);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    nameChanges.add(new AbstractMap.SimpleEntry<>(set.getInt("timestamp"), set.getString("new_name")));
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }

        return nameChanges;
    }


    public void setRank(int userId, int rankId) throws Exception {
        Habbo habbo = this.getHabbo(userId);

        if (!Emulator.getGameEnvironment().getPermissionsManager().groupExists(rankId)) {
            throw new Exception("Rank ID (" + rankId + ") does not exist");
        }
        PermissionGroup newPermissionGroup = Emulator.getGameEnvironment().getPermissionsManager().getGroup(rankId);
        if (habbo != null && habbo.getHabboStats() != null) {
            PermissionGroup oldPermissionGroup = habbo.getHabboInfo().getPermissionGroup();
            if (!oldPermissionGroup.getBadge().isEmpty()) {
                habbo.deleteBadge(habbo.getInventory().getBadgesComponent().getBadge(oldPermissionGroup.getBadge()));
            }
            if(oldPermissionGroup.getRoomEffect() > 0) {
                habbo.getInventory().getEffectsComponent().getEffects().remove(oldPermissionGroup.getRoomEffect());
            }

            habbo.getHabboInfo().setPermissionGroup(newPermissionGroup);

            if (!newPermissionGroup.getBadge().isEmpty()) {
                habbo.addBadge(newPermissionGroup.getBadge());
            }

            if(newPermissionGroup.getRoomEffect() > 0) {
                habbo.getInventory().getEffectsComponent().createRankEffect(habbo.getHabboInfo().getPermissionGroup().getRoomEffect());
            }

            habbo.getClient().sendResponse(new UserRightsMessageComposer(habbo));
            habbo.getClient().sendResponse(new UserPerksComposer(habbo));

            if (habbo.hasRight(Permission.ACC_SUPPORTTOOL)) {
                habbo.getClient().sendResponse(new ModeratorInitMessageComposer(habbo));
            }
            habbo.getHabboInfo().run();

            habbo.getClient().sendResponse(new CatalogPublishedMessageComposer());
            habbo.getClient().sendResponse(new BuildersClubFurniCountMessageComposer(0));
            habbo.getClient().sendResponse(new BundleDiscountRulesetMessageComposer());
            habbo.getClient().sendResponse(new MarketplaceConfigurationComposer());
            habbo.getClient().sendResponse(new GiftWrappingConfigurationComposer());
            habbo.getClient().sendResponse(new RecyclerPrizesComposer());
            habbo.alert(Emulator.getTexts().getValue("commands.generic.cmd_give_rank.new_rank").replace("id", newPermissionGroup.getName()));
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET `rank` = ? WHERE id = ? LIMIT 1")) {
                statement.setInt(1, rankId);
                statement.setInt(2, userId);
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }

        Emulator.getPluginManager().fireEvent(new UserRankChangedEvent(habbo));
    }

    public void giveCredits(int userId, int credits) {
        Habbo habbo = this.getHabbo(userId);
        if (habbo != null) {
            habbo.giveCredits(credits);
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET credits = credits + ? WHERE id = ? LIMIT 1")) {
                statement.setInt(1, credits);
                statement.setInt(2, userId);
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
    }

    public void staffAlert(String message) {
        message = Emulator.getTexts().getValue("commands.generic.cmd_staffalert.title") + "\r\n" + message;
        ServerMessage msg = new HabboBroadcastMessageComposer(message).compose();
        Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(msg, "cmd_staffalert");
    }
}
