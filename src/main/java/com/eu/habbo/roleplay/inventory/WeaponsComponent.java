package com.eu.habbo.roleplay.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.users.HabboWeapon;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeaponsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponsComponent.class);

    private final THashMap<Integer, HabboWeapon> weapons = new THashMap<Integer, HabboWeapon>();

    private HabboWeapon equippedWeapon;

    public HabboWeapon getEquippedWeapon() {
        return this.equippedWeapon;
    }

    public void setEquippedWeapon(HabboWeapon newEquippedWeapon) {
        this.equippedWeapon = newEquippedWeapon;
    }


    public final Habbo habbo;
    public WeaponsComponent(Habbo habbo) {
        this.habbo = habbo;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_users_weapons WHERE user_id = ?")) {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.weapons.put(set.getInt("weapon_id"), new HabboWeapon(set));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        if(habbo.getHabboInfo().getPermissionGroup().getRoomEffect() > 0)
            this.createWeapon(habbo.getHabboInfo().getPermissionGroup().getRoomEffect());
    }

    public HabboWeapon getWeaponByUniqueName(String uniqueName) {
        for (HabboWeapon weapon : weapons.values()) {
            if (weapon.getWeapon().getUniqueName().equals(uniqueName)) {
                return weapon;
            }
        }
        return null;
    }


    public HabboWeapon createWeapon(int effectId, int duration) {
        HabboWeapon weapon;
        synchronized (this.weapons) {
            if (this.weapons.containsKey(effectId)) {
                weapon = this.weapons.get(effectId);
            } else {
                weapon = new HabboWeapon(effectId, this.habbo.getHabboInfo().getId());
                weapon.insert();
            }

            this.addWeapon(weapon);
        }

        return weapon;
    }

    public HabboWeapon createWeapon(int weaponID) {
        HabboWeapon weapon = new HabboWeapon(weaponID, habbo.getHabboInfo().getId());
        this.weapons.put(weaponID, weapon);
        return weapon;
    }

    public void addWeapon(HabboWeapon weapon) {
        this.weapons.put(weapon.getWeaponID(), weapon);
    }

    public void dispose() {
        synchronized (this.weapons) {
            this.weapons.clear();
        }
    }

}