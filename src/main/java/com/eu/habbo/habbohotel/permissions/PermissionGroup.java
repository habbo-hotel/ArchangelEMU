package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PermissionGroup {
    @Getter
    private final int id;
    @Getter
    private final int level;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String badge;
    @Getter
    private final int roomEffect;
    @Getter
    private final boolean logEnabled;
    @Getter
    private final String prefix;
    @Getter
    private final String prefixColor;
    private final Map<String, PermissionSetting> commands;
    private final Map<String, PermissionSetting> rights;

    @Getter final Map<Integer, PermissionCurrencyTimer> timers;

    public PermissionGroup(ResultSet set, Map<String, PermissionCommand> commandsAvailable, Map<String, PermissionRight> rightsAvailable) throws SQLException {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.level = set.getInt("level");
        this.prefix = set.getString("prefix");
        this.prefixColor = set.getString("prefix_color");
        this.badge = set.getString("badge");
        this.roomEffect = set.getInt("room_effect");
        this.logEnabled = set.getString("log_enabled").equals("1");

        this.commands = new HashMap<>();
        this.rights = new HashMap<>();
        this.timers = new HashMap<>();

        this.loadCommands(commandsAvailable);
        this.loadRights(rightsAvailable);
        this.loadTimers();

        log.info("Loaded " + this.name + " rank with " + this.commands.size() + " commands and " + this.rights.size() + " rights!");
    }

    private void loadCommands(Map<String, PermissionCommand> commandsAvailable) {
        this.commands.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permission_group_commands WHERE group_id = ?")) {
            statement.setInt(1, this.id);
            try(ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String commandName = set.getString("command_name");
                    PermissionCommand command = commandsAvailable.values().stream().filter(commandAvailable -> commandAvailable.getName().equalsIgnoreCase(commandName)).findFirst().orElse(null);

                    if(command != null) {
                        PermissionSetting setting = PermissionSetting.fromString(set.getString("setting_type"));
                        this.commands.put(command.getName(), setting);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadRights(Map<String, PermissionRight> rightsAvailable) {
        this.rights.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permission_group_rights WHERE group_id = ?")) {
            statement.setInt(1, this.id);
            try(ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String rightName = set.getString("right_name");
                    PermissionRight right = rightsAvailable.values().stream().filter(rightAvailable -> rightAvailable.getName().equalsIgnoreCase(rightName)).findFirst().orElse(null);

                    if(right != null) {
                        this.rights.put(right.getName(), PermissionSetting.fromString(set.getString("setting_type")));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadTimers() {
        this.timers.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permission_group_timers WHERE group_id = ? ORDER BY id ASC")) {
            statement.setInt(1, this.id);
            try(ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    PermissionCurrencyTimer currencyTimer = new PermissionCurrencyTimer(set);
                    this.timers.put(currencyTimer.getCurrencyType(), currencyTimer);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public boolean canExecuteCommand(String commandName, boolean hasRoomRights) {
        PermissionsManager permissionsManager = Emulator.getGameEnvironment().getPermissionsManager();
        PermissionCommand command = permissionsManager.getCommandByKey(commandName);

        if(command == null) {
            return false;
        }

        if (permissionsManager.isFixedCommand(command.getName())) {
            return true;
        }

        if(this.commands.containsKey(command.getName())) {
            PermissionSetting setting = this.commands.get(command.getName());

            if(setting == null) {
                return false;
            }

            return setting == PermissionSetting.ALLOWED || (setting == PermissionSetting.HAS_ROOM_RIGHTS && hasRoomRights);
        }

        return false;
    }

    public boolean hasPermissionRight(String rightName, boolean hasRoomRights) {
        if(this.rights.containsKey(rightName)) {
            PermissionSetting setting = this.rights.get(rightName);
            return setting == PermissionSetting.ALLOWED || setting == PermissionSetting.HAS_ROOM_RIGHTS && hasRoomRights;
        }

        return false;
    }

    public int getTimerAmount(int currencyType) {
        if(this.timers.containsKey(currencyType)) {
            return this.timers.get(currencyType).getAmount();
        }

        return 0;
    }

    public List<String> getCommands() {
        Collection<String> fixedCommands = Emulator.getGameEnvironment().getPermissionsManager().getFixedCommands();
        Set<String> commands = this.commands.keySet();

        return Stream.concat(fixedCommands.stream(), commands.stream()).collect(Collectors.toList());
    }

    public Set<String> getRights() {
        return this.rights.keySet();
    }

    public boolean hasPrefix() {
        return !this.prefix.isEmpty();
    }

    public boolean hasBadge() {
        return !this.badge.isEmpty();
    }

}

