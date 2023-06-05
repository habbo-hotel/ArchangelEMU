package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class PermissionsManager {
    private final Map<Integer, PermissionGroup> permissionGroups;
    private final Map<String, PermissionCommand> permissionCommands;
    private final Map<String, PermissionCommand> fixedCommands;
    private final Map<String, PermissionRight> permissionRights;
    private final TIntIntHashMap specialEnables;

    public PermissionsManager() {
        long millis = System.currentTimeMillis();
        this.permissionGroups = new HashMap<>();
        this.permissionCommands = new HashMap<>();
        this.fixedCommands = new HashMap<>();
        this.permissionRights = new HashMap<>();
        this.specialEnables = new TIntIntHashMap();
        this.reload();
        log.info("Permissions Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reload() {
        this.loadPermissionCommands();
        this.loadPermissionRights();
        this.loadPermissionGroups();
        this.loadEnables();
        log.info(this.permissionGroups.size() + " ranks, " + this.permissionCommands.size() + " commands " + this.permissionRights.size() + " rights -> Loaded!");
    }

    private void loadPermissionGroups() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM permission_groups ORDER BY id ASC")) {
            while (set.next()) {
                PermissionGroup permissionGroup = new PermissionGroup(set, this.permissionCommands);
                this.permissionGroups.put(permissionGroup.getId(), permissionGroup);
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadPermissionCommands() {
        this.loadFixedCommands();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM permission_commands")) {
            while (set.next()) {
                PermissionCommand permissionCommand = new PermissionCommand(set);
                this.permissionCommands.put(permissionCommand.getName(), permissionCommand);
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadFixedCommands() {
        String[] fixedCommandNames = {
                "cmd_about",
                "cmd_arcturus",
                "cmd_commands",
                "cmd_habnam",
                "cmd_lay",
                "cmd_mute_bots",
                "cmd_mute_pets",
                "cmd_plugins",
                "cmd_sit",
                "cmd_stand"
        };

        for(String command : fixedCommandNames) {
            String description = Emulator.getTexts().getValue("commands.description." + command, "commands.description." + command);
            String[] keys = Emulator.getTexts().getValue("commands.keys." + command).split(";");

            this.fixedCommands.put(command, new PermissionCommand(command, description, keys));
        }
    }

    public void addFixedCommand(PermissionCommand fixedCommand) {
        this.fixedCommands.put(fixedCommand.getName(), fixedCommand);
    }

    private void loadPermissionRights() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM permission_rights")) {
            while (set.next()) {
                PermissionRight permissionRight = new PermissionRight(set);
                this.permissionRights.put(permissionRight.getName(), permissionRight);
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    private void loadEnables() {
        synchronized (this.specialEnables) {
            this.specialEnables.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM special_enables")) {
                while (set.next()) {
                    this.specialEnables.put(set.getInt("effect_id"), set.getInt("min_rank"));
                }
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
    }


    public boolean groupExists(int groupId) {
        return this.permissionGroups.containsKey(groupId);
    }


    public PermissionGroup getGroup(int groupId) {
        return this.permissionGroups.get(groupId);
    }

    public PermissionGroup getGroupByName(String name) {
        return this.permissionGroups.values().stream().filter(group -> group.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public PermissionCommand getCommand(String name) {
        return this.fixedCommands.getOrDefault(name, this.permissionCommands.get(name));
    }

    public PermissionCommand getCommandByKey(String key) {
        return Stream.concat(this.fixedCommands.values().stream(), this.permissionCommands.values().stream())
                .filter(command -> command.hasKey(key))
                .findFirst()
                .orElseGet(() -> null);
    }

    public Collection<PermissionCommand> getFixedCommands() {
        return this.fixedCommands.values();
    }

    public boolean isFixedCommand(String name) {
        return this.fixedCommands.containsKey(name);
    }

    public PermissionRight getRight(String name) {
        return this.permissionRights.get(name);
    }

    public boolean isEffectBlocked(int effectId, int groupId) {
        return this.specialEnables.contains(effectId) && this.specialEnables.get(effectId) > groupId;
    }
}
