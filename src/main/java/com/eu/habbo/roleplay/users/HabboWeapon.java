package com.eu.habbo.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.roleplay.weapons.Weapon;
import com.eu.habbo.roleplay.weapons.WeaponsManager;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class HabboWeapon {

    private static final Logger LOGGER = LoggerFactory.getLogger(HabboWeapon.class);

    private Weapon weapon;

    private int weaponID;

    private int userID;

    @Setter
    private int currentAmmo;

    public HabboWeapon(ResultSet set) throws SQLException {
        this.weapon = WeaponsManager.getInstance().getWeaponByID(set.getInt("weapon_id"));
        this.weaponID = set.getInt("weapon_id");
        this.userID = set.getInt("user_id");
        this.currentAmmo = this.weapon.getAmmoCapacity();
    }

    public HabboWeapon(int weaponID, int userID) {
        this.weaponID = weaponID;
        this.userID = userID;
    }

    public void insert() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users_weapons (weapon_id, user_id) VALUES (?, ?)")) {
            statement.setInt(1, this.weaponID);
            statement.setInt(2, this.userID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public void delete() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_users_weapons WHERE weapon_id = ? AND user_id = ?")) {
            statement.setInt(1, this.weaponID);
            statement.setInt(2, this.userID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }
}