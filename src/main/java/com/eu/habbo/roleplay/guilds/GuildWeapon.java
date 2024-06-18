package com.eu.habbo.roleplay.guilds;


import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.weapons.Weapon;
import com.eu.habbo.roleplay.weapons.WeaponsManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class GuildWeapon {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.eu.habbo.roleplay.users.HabboWeapon.class);

    private Weapon weapon;

    private int weaponID;

    private int guildID;

    public GuildWeapon(ResultSet set) throws SQLException {
        this.weapon = WeaponsManager.getInstance().getWeaponByID(set.getInt("weapon_id"));
        this.weaponID = set.getInt("weapon_id");
        this.guildID = set.getInt("guild_id");
    }

    public GuildWeapon(int weaponID, int guildID) {
        this.weaponID = weaponID;
        this.guildID = guildID;
    }

    public void insert() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_guilds_weapons (weapon_id, guild_id) VALUES (?, ?)")) {
            statement.setInt(1, this.weaponID);
            statement.setInt(2, this.guildID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_guilds_weapons WHERE weapon_id = ? AND guild_id = ?")) {
            statement.setInt(1, this.weaponID);
            statement.setInt(2, this.guildID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}