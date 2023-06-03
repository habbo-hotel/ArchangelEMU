package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
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
    private final Map<PermissionCommand, PermissionSetting> commands;
    private final Map<PermissionRight, PermissionSetting> rights;

    @Getter final Map<Integer, PermissionCurrencyTimer> timers;

    public PermissionGroup(ResultSet set, Map<String, PermissionCommand> commandsAvailable) throws SQLException {
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
        this.loadRights();
        this.loadTimers();

        log.info("Loaded " + this.name + " rank with " + this.commands.size() + " commands and " + this.rights.size() + " rights!");
    }

    private void loadCommands(Map<String, PermissionCommand> commandsAvailable) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permission_group_commands WHERE group_id = ?")) {
            statement.setInt(1, this.id);
            try(ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String commandName = set.getString("command_name");
                    PermissionCommand command = commandsAvailable.values().stream().filter(commandAvailable -> commandAvailable.getName().equalsIgnoreCase(commandName)).findFirst().orElse(null);
                    this.commands.put(command, PermissionSetting.fromString(set.getString("setting_type")));
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadRights() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permission_group_rights WHERE group_id = ?")) {
            statement.setInt(1, this.id);
            try(ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    PermissionRight right = Emulator.getGameEnvironment().getPermissionsManager().getRight(set.getString("name"));
                    this.rights.put(right, PermissionSetting.fromString(set.getString("setting_type")));
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadTimers() {
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

    public boolean canExecuteCommand(String key, boolean hasRoomRights) {
        PermissionsManager permissionsManager = Emulator.getGameEnvironment().getPermissionsManager();
        PermissionCommand command = permissionsManager.getCommandByKey(key);

        if(command == null) {
            return false;
        }

        if (permissionsManager.isFixedCommand(command.getName())) {
            return true;
        }

        PermissionSetting setting = this.commands.get(command);
        return setting == PermissionSetting.ALLOWED || (setting == PermissionSetting.HAS_ROOM_RIGHTS && hasRoomRights);
    }

    public boolean hasCommand(String name, boolean hasRoomRights) {
        PermissionCommand command = Emulator.getGameEnvironment().getPermissionsManager().getCommand(name);
        if (this.commands.containsKey(command)) {
            PermissionSetting setting = this.commands.get(command);
            return (setting == PermissionSetting.ALLOWED || (setting == PermissionSetting.HAS_ROOM_RIGHTS && hasRoomRights));
        }
        return false;
    }

    public boolean hasRight(String name, boolean hasRoomRights) {
        PermissionRight right = Emulator.getGameEnvironment().getPermissionsManager().getRight(name);
        if(this.rights.containsKey(right)) {
            PermissionSetting setting = this.rights.get(right);
            return (setting == PermissionSetting.ALLOWED || (setting == PermissionSetting.HAS_ROOM_RIGHTS && hasRoomRights));
        }

        return false;
    }

    public int getTimerAmount(int currencyType) {
        if(this.timers.containsKey(currencyType)) {
            return this.timers.get(currencyType).getAmount();
        }

        return 0;
    }

    public List<PermissionCommand> getCommands() {
        Collection<PermissionCommand> fixedCommands = Emulator.getGameEnvironment().getPermissionsManager().getFixedCommands();
        Set<PermissionCommand> commands = this.commands.keySet();

        return Stream.concat(fixedCommands.stream(), commands.stream()).collect(Collectors.toList());
    }

    public Set<PermissionRight> getRights() {
        return this.rights.keySet();
    }

    public boolean hasPrefix() {
        return !this.prefix.isEmpty();
    }

    public boolean hasBadge() {
        return !this.badge.isEmpty();
    }

}

